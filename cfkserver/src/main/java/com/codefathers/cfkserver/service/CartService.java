package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.user.NoSuchAPackageException;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.user.Cart;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.entities.user.SubCart;
import com.codefathers.cfkserver.model.repositories.CartRepository;
import com.codefathers.cfkserver.model.repositories.SubCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private SubCartRepository subCartRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private SellerService sellerService;

    public void addProductToCart(Cart cart, String sellerId, int productId, int amount)
            throws ProductExistedInCart, NotEnoughAmountOfProductException, NoSuchAProductException, NotTheSellerException, NoSuchSellerException, NoSuchAPackageException, NoSuchSellerException {
        checkIfProductExistsInCart(cart, productId);
        checkIfThereIsEnoughAmountOfProduct(productId, sellerId, amount);
        Product product = productService.findById(productId);
        Seller seller;
        if (!sellerId.isEmpty()) {
            seller = sellerService.findSellerByUsername(sellerId);
            checkIfIsTheSellerOfThisProduct(product, seller);
        } else {
            seller = productService.bestSellerOf(product);
        }

        SubCart subCart = new SubCart(product, seller, amount);
        subCart.setCart(cart);

        cart.getSubCarts().add(subCart);
        cart.setTotalPrice(calculateTotalPrice(cart));

        subCartRepository.save(subCart);
        cartRepository.save(cart);
        DBManager.save(cart);
    }

    private void checkIfIsTheSellerOfThisProduct(Product product, Seller seller) throws NotTheSellerException {
        if (!product.hasSeller(seller)) throw new NotTheSellerException();
    }

    private long calculateTotalPrice(Cart cart) throws NoSuchAPackageException {
        long total = 0;
        CSCLManager csclManager = CSCLManager.getInstance();
        for (SubCart subCart : cart.getSubCarts()) {
            total += csclManager.findPrice(subCart);
        }
        return total;
    }

    void checkIfThereIsEnoughAmountOfProduct(int productId, String sellerId, int amount)
            throws NotEnoughAmountOfProductException, NoSuchAProductException, NoSuchSellerException {
        Product product = ProductManager.getInstance().findProductById(productId);
        int stock = product.findPackageBySeller(sellerId).getStock();
        if (amount > stock) throw new NotEnoughAmountOfProductException(amount);
    }

    private void checkIfProductExistsInCart(Cart cart, int productId) throws ProductExistedInCart {
        for (SubCart subCart : cart.getSubCarts()) {
            if (subCart.getProduct().getId() == productId) throw new ProductExistedInCart(productId);
        }
    }
}
