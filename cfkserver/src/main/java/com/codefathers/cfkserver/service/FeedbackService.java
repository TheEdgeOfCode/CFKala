package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.feedback.NotABuyer;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.user.NoSuchACustomerException;
import com.codefathers.cfkserver.model.entities.logs.PurchaseLog;
import com.codefathers.cfkserver.model.entities.product.Comment;
import com.codefathers.cfkserver.model.entities.product.Score;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.entities.user.Customer;
import com.codefathers.cfkserver.model.repositories.CommentRepository;
import com.codefathers.cfkserver.model.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private RequestService requestService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;

    public void createComment(Comment comment) throws NoSuchACustomerException {
        commentRepository.save(comment);
        String requestStr = String.format("User (%s) has requested to assign a comment on product (%d) :\n" +
                "Title : %s\n" +
                "Text : %20s ...", comment.getUserId(), comment.getProduct().getId(), comment.getTitle(), comment.getText());
        requestService.createRequest(comment, RequestType.ASSIGN_COMMENT, comment.getUserId(), requestStr);
        Customer customer = customerService.getCustomerByUsername(comment.getUserId());

        Request request = new Request(comment.getUserId(), RequestType.ASSIGN_COMMENT, requestStr, comment);
        customer.addRequest(request);
        customerService.save(customer);
    }

    public void createScore(String userId, int productId, int score)
            throws NoSuchAProductException, NotABuyer, NoSuchACustomerException {
        if (!boughtThisProduct(productId, userId)) throw new NotABuyer();
        Score SCORE = new Score(userId, productId, score);
        productService.assignAScore(productId, SCORE);
        scoreRepository.save(SCORE);
    }

    public boolean boughtThisProduct(int productId, String probableBuyerId) throws NoSuchACustomerException {
        Customer customer = customerService.getCustomerByUsername(probableBuyerId);
        for (PurchaseLog log : customer.getPurchaseLogs()) {
            if (log.containsProduct(productId)) return true;
        }
        return false;
    }
}
