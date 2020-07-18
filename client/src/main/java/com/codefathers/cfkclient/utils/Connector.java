package com.codefathers.cfkclient.utils;

import com.codefathers.cfkclient.dtos.category.CategoryPM;
import com.codefathers.cfkclient.dtos.category.CreateDTO;
import com.codefathers.cfkclient.dtos.content.AdPM;
import com.codefathers.cfkclient.dtos.content.CreateAd;
import com.codefathers.cfkclient.dtos.content.MainContent;
import com.codefathers.cfkclient.dtos.customer.*;
import com.codefathers.cfkclient.dtos.discount.*;
import com.codefathers.cfkclient.dtos.edit.*;
import com.codefathers.cfkclient.dtos.log.SellLogDTO;
import com.codefathers.cfkclient.dtos.log.SellLogListDTO;
import com.codefathers.cfkclient.dtos.off.CreateOffDTO;
import com.codefathers.cfkclient.dtos.product.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.json.GsonJsonParser;
import com.codefathers.cfkclient.dtos.user.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;

import static org.springframework.http.HttpMethod.*;

@ConfigurationProperties(prefix = "uri")
public class Connector {
    private String token;
    private String address;
    private RestTemplate restTemplate;
    private static Connector connector = new Connector();

    public static Connector getInstance() {
        return connector;
    }

    public Connector(){
        token = "";
        restTemplate = new RestTemplate();
    }

    private <T,U> ResponseEntity<U> post(String uri,T dto ,Class<U> type) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authentication","cfk! " + token);
        HttpEntity<T> requestEntity = new HttpEntity<>(dto,headers);
        ResponseEntity<U> response = restTemplate.exchange(uri, POST, requestEntity, type);
        if (response.getStatusCode().equals(HttpStatus.OK)){
            return response;
        }else {
            /*this might cause problem */
            String error = response.getStatusCode().toString() +  response.getHeaders().get("ERROR");
            throw new Exception(error);
        }
    }

    private <T,U> U get(String uri, T dto , Type type) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authentication","cfk! " + token);
        HttpEntity<T> requestEntity = new HttpEntity<>(dto,headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, GET, requestEntity, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK)){
            Gson gson = new Gson();
            return gson.fromJson(response.getBody(),type);
        }else {
            throw new Exception(new GsonJsonParser().parseMap(response.getBody()).get("error").toString());
        }
    }

    public List<MiniProductDto> getAllProducts(FilterSortDto dto) throws Exception {
        ResponseEntity<MiniProductArrayListDto> response = post("http://127.0.0.1:8050/product/get_all_products",
                dto, MiniProductArrayListDto.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public List<MainContent> mainContents() throws Exception {
        return get("http://127.0.0.1:8050/content/get_main_contents",null,new TypeToken<ArrayList<MainContent>>(){}.getType());
    }

    public List<AdPM> getAds() throws Exception {
        return get("http://127.0.0.1:8050/content/all_ads",null,new TypeToken<ArrayList<AdPM>>(){}.getType());
    }

    public String login(LoginDto dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post("http://127.0.0.1:8050/users/login", dto, TokenRoleDto.class);
        token = Objects.requireNonNull(role.getBody()).getToken();
        return role.getBody().getRole();
    }

    public void createCustomerAccount(CustomerDTO dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post("http://127.0.0.1:8050/users/create_customer", dto, TokenRoleDto.class);
        token = Objects.requireNonNull(role.getBody()).getToken();
    }

    public void createManagerAccount(ManagerDTO dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post("http://127.0.0.1:8050/users/create_manager", dto, TokenRoleDto.class);
    }
    public void createSellerAccount(SellerDTO dto) throws Exception {
        post("http://127.0.0.1:8050/users/create_seller", dto, String.class);
    }

    public UserFullDTO viewPersonalInfo() throws Exception {
        ResponseEntity<UserFullDTO> response = post("http://127.0.0.1:8050/users/view", null, UserFullDTO.class);
        return response.getBody();
    }

    public void editPersonalInfo(UserEditAttributes attributes) throws Exception {
        post("http://127.0.0.1:8050/users/edit", attributes, HttpStatus.class);
    }

    public CartDTO showCart() throws Exception {
        ResponseEntity<CartDTO> response = post("http://127.0.0.1:8050/customer/show_cart",
                null, CartDTO.class);
        return Objects.requireNonNull(response.getBody());
    }

    public void changeAmount(int productId,int amount) throws Exception {
        post(address + "/customer/change_amount", "" + productId +  "," + amount, String.class);
    }

    public void deleteProductFromCart(Integer productId) throws Exception {
        post("http://127.0.0.1:8050/customer/delete_product_from_cart",
                productId, String.class);
    }

    public void purchase(PurchaseDTO dto) throws Exception {
        post("http://127.0.0.1:8050/customer/purchase",
                dto, String.class);
    }

    public Long showPurchaseTotalPrice(String disCode) throws Exception {
        ResponseEntity<Long> response = post("http://127.0.0.1:8050/customer/purchase/show_total_price",
                disCode, Long.class);
        return Objects.requireNonNull(response).getBody();
    }

    public List<OrderLogDTO> showOrders() throws Exception {
        ResponseEntity<OrderLogListDTO> response = post("http://127.0.0.1:8050/customer/show_orders",
                null, OrderLogListDTO.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public void addViewDigest(Integer productId) throws Exception {
        post("http://127.0.0.1:8050/customer/add_view",
                productId, String.class);
    }

    public List<DisCodeUserDTO> showDiscountCodes() throws Exception {
        ResponseEntity<DisCodeUserListDTO> response = post("http://127.0.0.1:8050/customer/show_discounts",
                null, DisCodeUserListDTO.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public void assignAScore(String info) throws Exception {
        post("http://127.0.0.1:8050/customer/assign_score",
                info, String.class);
    }

    public CompanyDTO viewCompanyInfo() throws Exception {
        ResponseEntity<CompanyDTO> response = post("http://127.0.0.1:8050/seller/view_company", null, CompanyDTO.class);
        return response.getBody();
    }

    public List<SellLogDTO> viewSalesHistory() throws Exception {
        ResponseEntity<SellLogListDTO> response = post("http://127.0.0.1:8050/seller/sellLog",
                null, SellLogListDTO.class);
        return Objects.requireNonNull(response.getBody()).getSellLogDTOList();
    }

    public void becomeSellerOfExistingProduct(AddSellerToProductDTO dto) throws Exception {
        post("http://127.0.0.1:8050/seller/become_seller", dto, HttpStatus.class);
    }

    public Long viewBalance() throws Exception {
        ResponseEntity<Long> response = get("http://127.0.0.1:8050/seller/balance",
                null, Long.class);
        return response.getBody();
    }

    // TODO: 7/18/2020 filters
    public List<MiniProductDto> manageSellerProducts() throws Exception {
        ResponseEntity<MiniProductArrayListDto> response = post("http://127.0.0.1:8050/seller/products",
                null, MiniProductArrayListDto.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public void editCategory(CategoryEditAttribute attribute) throws Exception {
        post("http://127.0.0.1:8050/category/edit",attribute,String.class);
    }

    public void removeCategory(Integer id) throws Exception {
        post("http://127.0.0.1:8050/category/remove",id,String.class);
    }

    public void addCategory(CreateDTO createDTO) throws Exception {
        post("http://127.0.0.1:8050/category/remove",createDTO,String.class);
    }

    public ArrayList<String> getSpecialFeatureOfCategory(Integer id) throws Exception {
        return get("http://127.0.0.1:8050/category/get_special",id,new TypeToken<ArrayList<String>>(){}.getType());
    }

    public List<String> getPublicFeaturesOfCategory() throws Exception {
        return get("http://127.0.0.1:8050/category/get_public", null,
                new TypeToken<ArrayList<String>>(){}.getType());
    }

    public ArrayList<CategoryPM> getAllCategories() throws Exception {
        return get("",null,new TypeToken<ArrayList<CategoryPM>>(){}.getType());
    }

    public void addContent(String title, String content) throws Exception {
        post("http://127.0.0.1:8050/content/add_content",title + "~~~" + content,String.class);
    }

    public void deleteContent(Integer id) throws Exception {
        post("http://127.0.0.1:8050/content/delete",id,String.class);
    }

    public void systematicDiscount(CreateDiscountSystematic createDiscount) throws Exception {
        post("http://127.0.0.1:8050/discount/systematic",createDiscount,String.class);
    }

    public void removeDiscountCode(String discountCode) throws Exception {
        post("http://127.0.0.1:8050/discount/delete",discountCode,String.class);
    }

    public void editDiscountCode(DiscountCodeEditAttributes attributes) throws Exception {
        post("http://127.0.0.1:8050/discount/edit",attributes,String.class);
    }

    public void createDiscount(CreateDiscount dto) throws Exception {
        post("http://127.0.0.1:8050/discount/create",dto,String.class);
    }

    public void removeUserFromDiscountCodeUsers(String code, String username) throws Exception {
        post("http://127.0.0.1:8050/discount/remove_user",code + "~~~" + username,String.class);
    }

    public void addUserToDiscountCode(AddUser dto) throws Exception {
        post("http://127.0.0.1:8050/discount/add_user",dto,String.class);
    }

    public ArrayList<DisCodeManagerPM> getDiscountCodes() throws Exception {
        return get("http://127.0.0.1:8050/discount/get_discounts",null,
                new TypeToken<ArrayList<DisCodeManagerPM>>(){}.getType());
    }

    public void addOff(CreateOffDTO dto) throws Exception {
        post("http://127.0.0.1:8050/off/create", dto, String.class);
    }

    public void editOff(OffChangeAttributes dto) throws Exception {
        post("http://127.0.0.1:8050/off/edit", dto, String.class);
    }

    public void removeOff(Integer id) throws Exception {
        post("http://127.0.0.1:8050/off/remove", id, String.class);
    }

    public int createProduct(CreateProductDTO dto) throws Exception {
        ResponseEntity<Integer> post = post("http://127.0.0.1:8050/products/create", dto, Integer.class);
        return post.getBody();
    }

    public Image userImage(String text) throws Exception {
        ByteArrayResource image = get(address + "/download/user/profile/" + text,null,
                ByteArrayResource.class);
        if (image != null) {
            return new Image(new ByteArrayInputStream(image.getInputStream().readAllBytes()));
        }else {
            return null;
        }
    }

    public void removeProduct(Integer id) throws Exception {
        post("http://127.0.0.1:8050/off/edit", id, String.class);
    }

    public List<UserFullDTO> showUsers() throws Exception {
        UserFullListDTO response = get("http://127.0.0.1:8050/manager/show_users",
                null, UserFullListDTO.class);
        return response.getDtos();
    }

    public void deleteUser(String username) throws Exception {
        post("http://127.0.0.1:8050/manager/delete_user", username, String.class);
    }

    public List<MiniProductDto> showProducts_Manager(FilterSortDto filterSortDto) throws Exception {
        return get("http://127.0.0.1:8050/manager/show_products",
                filterSortDto, MiniProductArrayListDto.class);
    }

    public void removeProduct_Manager(String id) throws Exception {
        post("http://127.0.0.1:8050/manager/remove_product", id, String.class);
    }

    public List<RequestDTO> showRequests() throws Exception {
        RequestsListDTO response = get("http://127.0.0.1:8050/manager/show_requests",
                null, RequestsListDTO.class);
        return response.getDtos();
    }

    public void acceptRequest(String id) throws Exception {
        post("http://127.0.0.1:8050/manager/accept_request", id, String.class);
    }

    public void declineRequest(String id) throws Exception {
        post("http://127.0.0.1:8050/manager/decline_request", id, String.class);
    }

    public List<MicroProductDto> sellerMicroProduct(String seller) throws Exception {
        return get(address + "/seller/ad/micro/" + seller,null,
                new TypeToken<ArrayList<MicroProductDto>>(){}.getType());
    }


    public void addAd(CreateAd ad) throws Exception {
        post(address + "/content/add_ad",ad,String.class);
    }

    public int createCompany(String[] info) throws Exception {
        ResponseEntity<Integer> response = post(address + "",info,Integer.class);
        return Objects.requireNonNull(response.getBody());
    }

    public ArrayList<MessagePM> getMessagesForUser() throws Exception {
        return get(address + "/messages/get_all",null,
                new TypeToken<ArrayList<MessagePM>>(){}.getType());
    }

    public void openMessage(int id) {
        try {
            post(address + "/messages/open/" + id,null,String.class);
        } catch (Exception ignore) {}
    }

    public void saveUserImage(InputStream stream) throws Exception {
        InputStreamResource resource = new InputStreamResource(stream);
        post(address + "/upload/user/profile", resource, String.class);
    }

    public void updateProductMainImage(int id,InputStream[] streams) throws Exception {
        post(address + "/upload/product/" + id,streams,String.class);
    }

    public Image productMainImage(int id) throws Exception {
        ByteArrayResource resource = get(address + "/download/product/" + id + "/main", null
                , ByteArrayResource.class);
        return createImageFromResource(resource);
    }

    public ArrayList<Image> loadImages(int id) throws Exception {
        ArrayList<Image> images = new ArrayList<>();
        ArrayList<ByteArrayResource> resources = get(address + "/download/product/" + id,null,
                new TypeToken<ArrayList<ByteArrayResource>>(){}.getType());
        resources.forEach(byteArrayResource -> {
            try {
                Image image = createImageFromResource(byteArrayResource);
                if (image != null) {
                    images.add(image);
                }
            } catch (IOException ignore) {}
        });
        return images;
    }

    private Image createImageFromResource(ByteArrayResource resource) throws IOException {
        if (resource != null) {
            return new Image(new ByteArrayInputStream(resource.getInputStream().readAllBytes()));
        }else {
            return null;
        }
    }

    public ArrayList<MicroProductDto> silmilarNameProducts(String name) throws Exception {
        return get(address + "/product/similar/" + name,null,
                new TypeToken<ArrayList<MicroProductDto>>(){}.getType());
    }
}
