package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.category.CategoryNotFoundException;
import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.product.*;
import com.codefathers.cfkserver.model.entities.product.Comment;
import com.codefathers.cfkserver.model.entities.product.CommentStatus;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.request.edit.ProductEditAttribute;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.service.*;
import com.codefathers.cfkserver.utils.TokenUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;
import static com.codefathers.cfkserver.utils.TokenUtil.getUsernameFromToken;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private FilterService filterService;
    @Autowired
    private Sorter sorter;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private FeedbackService feedbackService;


    @PostMapping("/product/get_all_products")
    private ResponseEntity<?> getAllProducts(@RequestBody FilterSortDto filterSortDto) {
        try {
            int[] priceRange = {filterSortDto.getDownPriceLimit(), filterSortDto.getUpPriceLimit()};
            List<Product> products = sorter.sort(filterService.updateFilterList(
                    filterSortDto.getCategoryId(), filterSortDto.getActiveFilters(), priceRange,
                    false, filterSortDto.isAvailableOnly()
                    )
                    , filterSortDto.getSortType());
            List<MiniProductDto> toReturn = dtosFromList(products);
            return ResponseEntity.ok(new MiniProductListDto(new ArrayList<>(toReturn)));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    private List<MiniProductDto> dtosFromList(List<Product> products) {
        List<MiniProductDto> toReturn = new ArrayList<>();
        products.forEach(product -> toReturn.add(dtoFromProduct(product)));
        return toReturn;
    }

    static MiniProductDto dtoFromProduct(Product product) {
        List<SellPackageDto> sellPackages = new ArrayList<>();
        product.getPackages().forEach(sellPackage -> {
            int offPercent = sellPackage.isOnOff() ? sellPackage.getOff().getOffPercentage() : 0;
            sellPackages.add(new SellPackageDto(offPercent,
                    sellPackage.getPrice(),
                    sellPackage.getStock(),
                    sellPackage.getSeller().getUsername(),
                    sellPackage.isAvailable()));
        });
        return new MiniProductDto(product.getName(), product.getId(),
                product.getCategory().getName(), sellPackages, product.getCompanyClass().getName(),
                product.getTotalScore(), product.getDescription(), product.isAvailable());
    }

    @PostMapping("/products/create")
    public ResponseEntity<?> addProduct(@RequestBody CreateProductDTO dto, HttpServletRequest request, HttpServletResponse response) {
        //TODO: Check if this works properly!!! (Two method for this...)
        try {
            if (checkToken(response,request)){
                try {
                    int id = productService.createProduct(dto);
                    return ResponseEntity.ok(id);
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
                    return null;
                }
            }
            return null;
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
            return null;
        }
    }

    @PostMapping("/products/edit")
    public void editProduct(@RequestBody ProductEditAttribute editAttribute,
                            HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response,request)){
                try {
                    productService.editProduct(getUsernameFromToken(request), editAttribute);
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
        }
    }

    @GetMapping
    @RequestMapping("/product/similar/{name}")
    public ResponseEntity<?> similarProducts(@PathVariable String name){
        return ResponseEntity.ok(productService.findProductsByName(name));
    }

    @GetMapping
    @RequestMapping("/products/full/{id}")
    private ResponseEntity<?> getFullProduct(@PathVariable Integer id, HttpServletResponse response){
        try {
            Product product = productService.findById(id);
            return ResponseEntity.ok(createFullProduct(product));
        } catch (NoSuchAProductException e) {
            sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
            return null;
        }
    }

    private FullProductPM createFullProduct(Product product) {
        MiniProductDto miniProductDto = dtoFromProduct(product);
        Map<String,String> features = new HashMap<>();
        features.putAll(product.getPublicFeatures());
        features.putAll(product.getSpecialFeatures());
        return new FullProductPM(miniProductDto,features);
    }

    @GetMapping
    @RequestMapping("/product/comments/{id}")
    private ResponseEntity<?> getComments(HttpServletResponse response, @PathVariable Integer id){
        try {
            ArrayList<Comment> comments = productService.getAllComment(id);
            ArrayList<CommentPM> toReturn = new ArrayList<>();
            comments.forEach(comment -> toReturn.add(
                    new CommentPM(comment.getUserId(),
                            comment.getTitle(),
                            comment.getText(),
                            comment.isBoughtThisProduct()))
            );
            return ResponseEntity.ok(toReturn);
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
            return null;
        }
    }

    @PostMapping("/product/add_to_cart")
    private void addToCart(@RequestBody String[] data,HttpServletRequest request, HttpServletResponse response) {
        try {
            checkToken(response, request);
            String userName = data[0];
            User user = userService.getUserByUsername(userName);
            int productId = Integer.parseInt(data[1]);
            String sellerUserName = data[2];
            int amount = Integer.parseInt(data[3]);
            cartService.addProductToCart(user.getCart(), sellerUserName, productId, amount);
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
        } catch (Exception e){
            sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @PostMapping("/product/comment/add")
    private void addComment(@RequestBody String[] data,HttpServletRequest request, HttpServletResponse response) {
        try {
            checkToken(response, request);
            String userId = data[0];
            String commentTitle = data[1];
            String commentText = data[2];
            int productId = Integer.parseInt(data[3]);
            Comment comment = new Comment(userId, commentTitle, commentText,
                    CommentStatus.NOT_VERIFIED,
                    feedbackService.boughtThisProduct(productId, userId));
            comment.setProduct(productService.findById(productId));
            feedbackService.createComment(comment);
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
        } catch (Exception e){
            sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @GetMapping("/products/on_off")
    private List<OffProductPM> getAllOnOff(@RequestBody FilterSortDto filter){
        List<SellPackage> allSellPackagesOnOff = productService.getOffPackages();
        List<SellPackage> filtered = filterService.filterSellPackages(filter.getCategoryId(), allSellPackagesOnOff,
                filter.getActiveFilters(),
                new int[]{filter.getDownPriceLimit(), filter.getUpPriceLimit()}, filter.isAvailableOnly());
        filtered = filterSellPackagesField(filtered, filter);
        new Sorter().sortSellPackage(filtered, filter.getSortType());
        if (!filter.isAscending()) Collections.reverse(filtered);
        List<OffProductPM> toReturn = new ArrayList<>();
        filtered.forEach(sellPackage -> {
            OffProductPM offProductPM = createOffPM(sellPackage);
            toReturn.add(offProductPM);
        });
        return toReturn;
    }

    private List<SellPackage> filterSellPackagesField(List<SellPackage> sellPackages, FilterSortDto filterPackage) {
        String name = filterPackage.getName();
        String seller = filterPackage.getSeller();
        String brand = filterPackage.getBrand();
        List<SellPackage> list = new CopyOnWriteArrayList<>(sellPackages);
        if (name != null)
            for (SellPackage product : list) {
                if (!product.getProduct().getName().toLowerCase().contains(name.toLowerCase())) list.remove(product);
            }
        if (seller != null)
            for (SellPackage product : list) {
                if (!product.getSeller().getUsername().toLowerCase().contains(seller.toLowerCase()))
                    list.remove(product);
            }
        if (brand != null)
            for (SellPackage product : list) {
                if (!product.getProduct().getCompanyClass().getName().toLowerCase().contains(brand.toLowerCase()))
                    list.remove(product);
            }
        return list;
    }

    private OffProductPM createOffPM(SellPackage sellPackage) {
        int price = sellPackage.getPrice();
        int percent = sellPackage.getOff().getOffPercentage();
        String name = sellPackage.getProduct().getName();
        int id = sellPackage.getProduct().getId();
        Date end = sellPackage.getOff().getEndTime();
        return new OffProductPM(name, id, price * (100 - percent) / 100, percent, end);
    }


    @PostMapping("/products/create/file")
    private ResponseEntity<?> createFileProduct(@RequestBody CreateDocumentDto documentDto
            ,HttpServletRequest request, HttpServletResponse response){
        try {
            checkToken(response, request);
            int id = productService.createFileProduct(documentDto);
            return ResponseEntity.ok(Integer.toString(id));
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
            return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
            return null;
        }
    }
}