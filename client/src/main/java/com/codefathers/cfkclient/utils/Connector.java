package com.codefathers.cfkclient.utils;

import com.codefathers.cfkclient.dtos.log.PurchaseLogDTOList;
import com.codefathers.cfkclient.dtos.log.PurchaseLogDTO;
import com.codefathers.cfkclient.dtos.auction.CreateAuctionDTO;
import com.codefathers.cfkclient.dtos.auction.AuctionListDTO;
import com.codefathers.cfkclient.dtos.auction.MiniAuctionDTO;
import com.codefathers.cfkclient.dtos.bank.*;
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
import com.codefathers.cfkclient.dtos.off.OffDTO;
import com.codefathers.cfkclient.dtos.off.OffListDTO;
import com.codefathers.cfkclient.dtos.product.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.json.GsonJsonParser;
import com.codefathers.cfkclient.dtos.user.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;

import static org.springframework.http.HttpMethod.*;

@ConfigurationProperties(prefix = "uri")
public class Connector {
    private String token;
    private String address = "http://127.0.0.1:8050";
    private String bankToken;
    private RestTemplate restTemplate;
    private static Connector connector = new Connector();

    public static Connector getInstance() {
        return connector;
    }

    public Connector(){
        token = "";
        bankToken = "";
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
        String body = response.getBody();
        if (response.getStatusCode().equals(HttpStatus.OK)){
            Gson gson = new Gson();
            return gson.fromJson(body,type);
        }else {
            throw new Exception(new GsonJsonParser().parseMap(body).get("error").toString());
        }
    }

    private<T> ByteArrayResource getResources(String uri,T dto) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authentication","cfk! " + token);
        HttpEntity<T> requestEntity = new HttpEntity<>(dto,headers);
        ResponseEntity<ByteArrayResource> responseEntity = restTemplate.exchange(uri,GET,requestEntity,ByteArrayResource.class);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)){
            return responseEntity.getBody();
        }else {
            String error = responseEntity.getStatusCode().toString() +  responseEntity.getHeaders().get("ERROR");
            throw new Exception(error);
        }
    }

    private<T> ByteArrayResource[] getArrayResources(String uri, T dto) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authentication","cfk! " + token);
        HttpEntity<T> requestEntity = new HttpEntity<>(dto,headers);
        ResponseEntity<ByteArrayResource[]> responseEntity = restTemplate.exchange(uri,GET,requestEntity, ByteArrayResource[].class);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)){
            return responseEntity.getBody();
        }else {
            String error = responseEntity.getStatusCode().toString() +  responseEntity.getHeaders().get("ERROR");
            throw new Exception(error);
        }
    }

    public List<MiniProductDto> getAllProducts(FilterSortDto dto) throws Exception {
        ResponseEntity<MiniProductArrayListDto> response = post(address + "/product/get_all_products",
                dto, MiniProductArrayListDto.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public List<MainContent> mainContents() throws Exception {
        return get(address + "/content/get_main_contents",null,new TypeToken<ArrayList<MainContent>>(){}.getType());
    }

    public List<AdPM> getAds() throws Exception {
        return get(address + "/content/all_ads",null,new TypeToken<ArrayList<AdPM>>(){}.getType());
    }

    public String login(LoginDto dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post(address + "/users/login", dto, TokenRoleDto.class);
        token = Objects.requireNonNull(role.getBody()).getToken();
        getToken(new TokenRequestDTO(dto.getUsername(), dto.getPassword()));
        return role.getBody().getRole();
    }

    public void createCustomerAccount(CustomerDTO dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post(address + "/users/create_customer", dto, TokenRoleDto.class);
        token = Objects.requireNonNull(role.getBody()).getToken();
        getToken(new TokenRequestDTO(dto.getUsername(), dto.getPassword()));
    }

    public void createManagerAccount(ManagerDTO dto) throws Exception {
        post(address + "/users/create_manager", dto, TokenRoleDto.class);
        getToken(new TokenRequestDTO(dto.getUsername(), dto.getPassword()));
    }

    public void createSellerAccount(SellerDTO dto) throws Exception {
        post(address + "/users/create_seller", dto, String.class);
    }

    public UserFullDTO viewPersonalInfo() throws Exception {
        return get(address + "/users/view", null, UserFullDTO.class);
    }

    public void editPersonalInfo(UserEditAttributes attributes) throws Exception {
        post(address + "/users/edit", attributes, HttpStatus.class);
    }

    public Long showBalance_Customer() throws Exception {
        ResponseEntity<Long> response = get(address + "/customer/show_balance",
                null, Long.class);
        return Objects.requireNonNull(response.getBody());
    }

    public CartDTO showCart() throws Exception {
        ResponseEntity<CartDTO> response = post(address + "/customer/show_cart",
                null, CartDTO.class);
        return Objects.requireNonNull(response.getBody());
    }

    public void changeAmount(int productId, int amount) throws Exception {
        post(address + "/customer/change_amount", "" + productId + "," + amount, String.class);
    }

    public void deleteProductFromCart(Integer productId) throws Exception {
        post(address + "/customer/delete_product_from_cart",
                productId, String.class);
    }

    public void purchase(PurchaseDTO dto) throws Exception {
        dto.setToken(bankToken);
        post(address + "/customer/purchase",
                dto, String.class);
    }

    public Long showPurchaseTotalPrice(String disCode) throws Exception {
        ResponseEntity<Long> response = post(address + "/customer/purchase/show_total_price",
                disCode, Long.class);
        return Objects.requireNonNull(response).getBody();
    }

    public List<OrderLogDTO> showOrders() throws Exception {
        ResponseEntity<OrderLogListDTO> response = post(address + "/customer/show_orders",
                null, OrderLogListDTO.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public void addViewDigest(Integer productId) throws Exception {
        post(address + "/customer/add_view",
                productId, String.class);
    }

    public List<DisCodeUserDTO> showDiscountCodes() throws Exception {
        ResponseEntity<DisCodeUserListDTO> response = post(address + "/customer/show_discounts",
                null, DisCodeUserListDTO.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public void assignAScore(String info) throws Exception {
        post(address + "/customer/assign_score",
                info, String.class);
    }

    public CompanyDTO viewCompanyInfo() throws Exception {
        ResponseEntity<CompanyDTO> response = post(address + "/seller/view_company", null, CompanyDTO.class);
        return response.getBody();
    }

    public List<SellLogDTO> viewSalesHistory() throws Exception {
        ResponseEntity<SellLogListDTO> response = post(address + "/seller/sellLog",
                null, SellLogListDTO.class);
        return Objects.requireNonNull(response.getBody()).getSellLogDTOList();
    }

    public void becomeSellerOfExistingProduct(AddSellerToProductDTO dto) throws Exception {
        post(address + "/seller/become_seller", dto, HttpStatus.class);
    }

    public Long viewBalance() throws Exception {
        ResponseEntity<Long> response = get(address + "/seller/balance",
                null, Long.class);
        return response.getBody();
    }

    public List<MiniProductDto> manageSellerProducts(FilterSortDto filterSortDto) throws Exception {
        ResponseEntity<MiniProductArrayListDto> response = post(address + "/seller/products",
                filterSortDto, MiniProductArrayListDto.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public void editCategory(CategoryEditAttribute attribute) throws Exception {
        post(address + "/category/edit", attribute, String.class);
    }

    public void removeCategory(Integer id) throws Exception {
        post(address + "/category/remove", id, String.class);
    }

    public void addCategory(CreateDTO createDTO) throws Exception {
        post(address + "/category/add", createDTO, String.class);
    }

    public ArrayList<String> getSpecialFeatureOfCategory(Integer id) throws Exception {
        return get(address + "/category/get_special/" + id, id, new TypeToken<ArrayList<String>>() {}.getType());
    }

    public List<String> getPublicFeaturesOfCategory() throws Exception {
        return get(address + "/category/get_public", null,
                new TypeToken<ArrayList<String>>() {}.getType());
    }

    public ArrayList<CategoryPM> getAllCategories() throws Exception {
        return get(address + "/category/get_all", null, new TypeToken<ArrayList<CategoryPM>>() {
        }.getType());
    }

    public void addContent(String title, String content) throws Exception {
        post(address + "/content/add_content", title + "~~~" + content, String.class);
    }

    public void deleteContent(Integer id) throws Exception {
        post(address + "/content/delete", id, String.class);
    }

    public void systematicDiscount(CreateDiscountSystematic createDiscount) throws Exception {
        post(address + "/discount/systematic", createDiscount, String.class);
    }

    public void removeDiscountCode(String discountCode) throws Exception {
        post(address + "/discount/delete", discountCode, String.class);
    }

    public void editDiscountCode(DiscountCodeEditAttributes attributes) throws Exception {
        post(address + "/discount/edit", attributes, String.class);
    }

    public void createDiscount(CreateDiscount dto) throws Exception {
        post(address + "/discount/create", dto, String.class);
    }

    public void removeUserFromDiscountCodeUsers(String code, String username) throws Exception {
        post(address + "/discount/remove_user",code + "~~~" + username,String.class);
    }

    public void addUserToDiscountCode(AddUser dto) throws Exception {
        post(address + "/discount/add_user",dto,String.class);
    }

    public ArrayList<DisCodeManagerPM> getDiscountCodes() throws Exception {
        return get(address + "/discount/get_discounts",null,
                new TypeToken<ArrayList<DisCodeManagerPM>>(){}.getType());
    }

    public void addOff(CreateOffDTO dto) throws Exception {
        post(address + "/off/create", dto, String.class);
    }

    public void editOff(OffChangeAttributes dto) throws Exception {
        post(address + "/off/edit", dto, String.class);
    }

    public void removeOff(Integer id) throws Exception {
        post(address + "/off/remove", id, String.class);
    }

    public int createProduct(CreateProductDTO dto) throws Exception {
        ResponseEntity<Integer> post = post(address + "/products/create", dto, Integer.class);
        return post.getBody();
    }

    public void sellerRemoveProduct(Integer id) throws Exception {
        post(address + "/seller/remove_product", id, String.class);
    }

    public List<UserFullDTO> showUsers() throws Exception {
        UserFullListDTO response = get(address + "/manager/show_users",
                null, UserFullListDTO.class);
        return response.getDtos();
    }

    public void deleteUser(String username) throws Exception {
        post(address + "/manager/delete_user", username, String.class);
    }

    public List<MiniProductDto> showProducts_Manager(FilterSortDto filterSortDto) throws Exception {
        return post(address + "/manager/show_products",
                filterSortDto, MiniProductArrayListDto.class).getBody().getDtos();
    }

    public void removeProduct_Manager(String id) throws Exception {
        post(address + "/manager/remove_product/" + id, id, String.class);
    }

    public List<RequestDTO> showRequests() throws Exception {
        RequestsListDTO response = get(address + "/manager/show_requests",
                null, RequestsListDTO.class);
        return response.getDtos();
    }

    public void acceptRequest(String id) throws Exception {
        post(address + "/manager/accept_request", id, String.class);
    }

    public void declineRequest(String id) throws Exception {
        post(address + "/manager/decline_request", id, String.class);
    }

    public List<MicroProductDto> sellerMicroProduct(String seller) throws Exception {
        return get(address + "/seller/ad/micro/" + seller,null,
                new TypeToken<ArrayList<MicroProductDto>>(){}.getType());
    }


    public void addAd(CreateAd ad) throws Exception {
        post(address + "/content/add_ad",ad,String.class);
    }

    public int createCompany(String[] info) throws Exception {
        ResponseEntity<Integer> response = post(address + "/users/create_company",info,Integer.class);
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

    public ArrayList<MicroProductDto> similarNameProducts(String name) throws Exception {
        return get(address + "/product/similar/" + name,null,
                new TypeToken<ArrayList<MicroProductDto>>(){}.getType());
    }

    public void editProduct(ProductEditAttribute attribute) throws Exception{
        post(address + "/products/edit",attribute,String.class);
    }

    public FullProductPM viewAttributes(int id) throws Exception {
        return get(address + "/products/full/" + id,null,FullProductPM.class);
    }

    public List<CommentPM> viewProductComments(int id) throws Exception {
        return get(address + "/product/comments/" + id,null,
                new TypeToken<ArrayList<CommentPM>>(){}.getType());
    }

    public void addToCart(String[] info) throws Exception {
        post(address + "/product/add_to_cart",info,String.class);
    }

    public void assignComment(String[] info) throws Exception {
        post(address + "/product/comment/add",info,String.class);
    }

    public List<OffProductPM> showAllOnOffProducts(FilterSortDto filter) throws Exception {
        return get(address + "products/on_off",filter,
                new TypeToken<ArrayList<OffProductPM>>(){}.getType());
    }

    public void logout(){
        try {
            post(address + "/users/logout",null,String.class);
        } catch (Exception ignore) {}
    }

    public List<OffDTO> viewAllOffs() throws Exception {
        OffListDTO listDTO = get(address + "/seller/offs", null, OffListDTO.class);
        return listDTO.getOffs();
    }

    public List<PurchaseLogDTO> viewAllLogs() throws Exception {
        PurchaseLogDTOList listDTO = get(address + "/manager/show_all_logs", null, PurchaseLogDTOList.class);
        return listDTO.getDtos();
    }

    public boolean isTheFirstManager() {
        try {
            return get(address + "/manager/is_first",null,Boolean.class);
        } catch (Exception ignore) { return false;}
    }


    public void getToken(TokenRequestDTO dto) throws Exception {
        bankToken = get(address + "/bank/get_token/" + dto.getUsername(), null, String.class);
    }

    public void chargeWallet(ChargeWalletDTO dto) throws Exception {
        dto.setToken(bankToken);
        post( address + "/users/charge_wallet", dto, String.class);
    }

    //TODO: We Do not need these!!!
    /*public String createBankAccount(CreateBankAccountDTO dto) throws Exception {
        ResponseEntity<String> response = post("http://127.0.0.1:8050/bank/create_account", dto, String.class);
        return Objects.requireNonNull(response.getBody());
    }*/
    /*public int createReceipt(CreateReceiptDTO dto) throws Exception {
        ResponseEntity<Integer> response = post("http://127.0.0.1:8050/bank/create_receipt",
                dto, Integer.class);
        return Objects.requireNonNull(response.getBody());
    }*/
    /*public void pay(String receiptId) throws Exception {
        post("http://127.0.0.1:8050/bank/pay", receiptId, String.class);
    }*/

    public int takeMoneyIntoAccount(TakeMoneyDTO dto) throws Exception {
        dto.setToken(bankToken);
        ResponseEntity<Integer> response = post(address + "/users/take_money", dto, Integer.class);
        return Objects.requireNonNull(response.getBody());
    }

    public List<TransactionDTO> getTransactions(NeededForTransactionDTO dto) throws Exception {
        dto.setToken(bankToken);
        ResponseEntity<TransactionListDTO> response = get("http://127.0.0.1:8050/bank/get_transactions",
                dto, TransactionListDTO.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public void pay(String receiptId) throws Exception {
        post("http://127.0.0.1:8050/bank/pay", receiptId, String.class);
    }

    public long getBalance(BalanceDTO dto) throws Exception {
        dto.setToken(bankToken);
        ResponseEntity<Long> response = get("http://127.0.0.1:8050/bank/get_balance",
                dto, Long.class);
        return Objects.requireNonNull(response.getBody());
    }

    public void createSupport(UserDTO userDTO) throws Exception {
        post(address + "/users/create/support",userDTO,String.class);
    }

    public void setThisSupportOnline() throws Exception {
        post(address + "/support/online",null,String.class);
    }

    public void setThisSupportOffline() throws Exception {
        post(address + "/support/offline",null,String.class);
    }

    public ArrayList<String> getAllSupports() throws Exception {
        return get(address + "/support/get/all",null,
                new TypeToken<ArrayList<String>>(){}.getType());
    }

    public String getGuestToken() throws Exception {
        return get(address + "/support/guest/get_account",null,String.class);
    }

    public void changeLogStatus(Integer logId) throws Exception {
        post(address + "/manager/change_log_status", logId, String.class);
    }

    /**Resources======================================================================================================*/

    public Image userImage() throws Exception {
        ByteArrayResource image = getResources(address + "/download/user/profile",null);
        return createImageFromResource(image);
    }

    public Image userImage(String username) throws Exception {
        ByteArrayResource image = getResources(address + "/download/user/profile/" + username ,null);
        return createImageFromResource(image);
    }

    public Image productMainImage(int id) throws Exception {
        ByteArrayResource resource = getResources(address + "/download/product/" + id + "/main", null);
        return createImageFromResource(resource);
    }

    public ArrayList<Image> loadImages(int id) throws Exception {
        ArrayList<Image> images = new ArrayList<>();
        ByteArrayResource[] resources = getArrayResources(address + "/download/product/" + id, null);
        Arrays.stream(resources).forEach(byteArrayResource -> {
            try {
                Image image = createImageFromResource(byteArrayResource);
                if (image != null) {
                    images.add(image);
                }
            } catch (IOException ignore) {
            }
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

    public InfoDTO getManagerInfoInBank() throws Exception {
        ResponseEntity<InfoDTO> response = get(address + "/bank/get_info",
                null, InfoDTO.class);
        return Objects.requireNonNull(response.getBody());
    }

    public void editTollMinimumBalanceInfo(TollMinimumBalanceEditAttribute attribute) throws Exception {
        post(address + "/bank/edit_info", attribute, String.class);
    }

    public void createAuction(CreateAuctionDTO dto) throws Exception {
        post(address + "/auction/create_auction", dto, String.class);
    }

    public List<MiniAuctionDTO> getAllAuctions() throws Exception {
        ResponseEntity<AuctionListDTO> response = get(
                address + "/auction/get_auctions",
                null,
                AuctionListDTO.class
        );
        return Objects.requireNonNull(response.getBody()).getDtos();
    }
}
