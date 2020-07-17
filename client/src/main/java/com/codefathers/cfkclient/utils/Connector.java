package com.codefathers.cfkclient.utils;

import com.codefathers.cfkclient.dtos.category.CategoryPM;
import com.codefathers.cfkclient.dtos.category.CreateDTO;
import com.codefathers.cfkclient.dtos.content.AdPM;
import com.codefathers.cfkclient.dtos.content.MainContent;
import com.codefathers.cfkclient.dtos.customer.*;
import com.codefathers.cfkclient.dtos.discount.DisCodeUserDTO;
import com.codefathers.cfkclient.dtos.edit.CategoryEditAttribute;
import com.codefathers.cfkclient.dtos.edit.UserEditAttributes;
import com.codefathers.cfkclient.dtos.log.SellLogListDTO;
import com.codefathers.cfkclient.dtos.product.FilterSortDto;
import com.codefathers.cfkclient.dtos.product.MiniProductArrayListDto;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;
import org.springframework.boot.json.GsonJsonParser;
import com.codefathers.cfkclient.dtos.user.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.*;

import static org.springframework.http.HttpMethod.*;

public class Connector {
    private String token;
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

    public void createCustomerAccount(CreateAccountDTO<CustomerDTO> dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post("http://127.0.0.1:8050/users/create_account", dto, TokenRoleDto.class);
        token = Objects.requireNonNull(role.getBody()).getToken();
        //TODO: Impl
    }

    public void createManagerAccount(CreateAccountDTO<ManagerDTO> dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post("http://127.0.0.1:8050/users/create_account", dto, TokenRoleDto.class);
        token = Objects.requireNonNull(role.getBody()).getToken();
        //TODO: Impl
    }
    public void createSellerAccount(CreateAccountDTO<SellerDTO> dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post("http://127.0.0.1:8050/users/create_account", dto, TokenRoleDto.class);
        //TODO: Impl
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

    public void changeAmount(String info) throws Exception {
        post("http://127.0.0.1:8050/customer/change_amount", info, String.class);
    }

    public List<InCartDTO> showProducts() throws Exception {
        ResponseEntity<InCartArrayListDTO> response = post("http://127.0.0.1:8050/customer/show_products",
                null, InCartArrayListDTO.class);
        return Objects.requireNonNull(response.getBody()).getInCartDTOS();
    }

    public void deleteProductFromCart(String info) throws Exception {
        post("http://127.0.0.1:8050/customer/delete_product_from_cart",
                info, String.class);
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

    public void addViewDigest(String productId) throws Exception {
        post("http://127.0.0.1:8050/customer/add_view",
                productId, String.class);
    }

    /*
    public List<DisCodeUserDTO> showDiscountCodes() throws Exception {
        ResponseEntity<DisCodeUserListDTO> response = post("http://127.0.0.1:8050/customer/show_discounts",
                null, DisCodeUserListDTO.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }*/

    public void assignAScore(String info) throws Exception {
        post("http://127.0.0.1:8050/customer/assign_score",
                info, String.class);
    }

    public CompanyDTO viewCompanyInfo() throws Exception {
        ResponseEntity<CompanyDTO> response = post("http://127.0.0.1:8050/seller/view_company", null, CompanyDTO.class);
        return response.getBody();
    }

    public SellLogListDTO viewSalesHistory() throws Exception {
        ResponseEntity<SellLogListDTO> response = post("http://127.0.0.1:8050/seller/sellLog",
                null, SellLogListDTO.class);
        return response.getBody();
    }

    public void becomeSellerOfExistingProduct() throws Exception {
        post("http://127.0.0.1:8050/seller/become_seller", null, HttpStatus.class);
    }

    public Long viewBalance() throws Exception {
        ResponseEntity<Long> response = post("http://127.0.0.1:8050/seller/balance",
                null, Long.class);
        return response.getBody();
    }

    public MiniProductArrayListDto manageSellerProducts() throws Exception {
        ResponseEntity<MiniProductArrayListDto> response = post("http://127.0.0.1:8050/seller/products",
                null, MiniProductArrayListDto.class);
        return response.getBody();
    }

    public Image userImage(String text) throws Exception {
        com.codefathers.cfkclient.dtos.user.Image image = get("http://127.0.0.1:8050users/getImage",null,
                com.codefathers.cfkclient.dtos.user.Image.class);
        if (image.getImage() != null) {
            return new Image(new ByteArrayInputStream(image.getImage()));
        }else {
            return null;
        }
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
        return get("http://127.0.0.1:805/category/get_special",id,new TypeToken<ArrayList<String>>(){}.getType());
    }

    public ArrayList<CategoryPM> getAllCategories() throws Exception {
        return get("",null,new TypeToken<ArrayList<CategoryPM>>(){}.getType());
    }
}
