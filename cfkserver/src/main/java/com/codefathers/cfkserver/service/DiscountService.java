package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.discount.*;
import com.codefathers.cfkserver.exceptions.model.off.InvalidTimesException;
import com.codefathers.cfkserver.model.entities.maps.DiscountcodeIntegerMap;
import com.codefathers.cfkserver.model.entities.maps.UserIntegerMap;
import com.codefathers.cfkserver.model.entities.offs.DiscountCode;
import com.codefathers.cfkserver.model.entities.request.edit.DiscountCodeEditAttributes;
import com.codefathers.cfkserver.model.entities.user.Customer;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DiscountService {
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private UserIntegerMapRepository userIntegerMapRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DiscountIntegerRepository discountIntegerRepository;

    public DiscountCode findByCode(String code) throws NoSuchADiscountCodeException {
        Optional<DiscountCode> discountCodeOptional = discountRepository.findById(code);
        if (discountCodeOptional.isPresent()) {
            return discountCodeOptional.get();
        } else {
            throw new NoSuchADiscountCodeException("There Isn't Code (" + code + ")");
        }
    }

    public boolean isDiscountAvailable(String code) throws NoSuchADiscountCodeException {
        DiscountCode discountCode = findByCode(code);
        Date date = new Date();
        Date startDate = discountCode.getStartTime();
        Date endDate = discountCode.getEndTime();
        return date.after(startDate) && date.before(endDate);
    }

    public void removeDiscount(String code) throws NoSuchADiscountCodeException {
        DiscountCode discountCode = findByCode(code);
        removeDiscount(discountCode);
    }

    void removeDiscount(DiscountCode discountCode) {
        deleteMapsForUsers(discountCode);
        discountRepository.delete(discountCode);
    }

    private void deleteMapsForUsers(DiscountCode discountCode) {
        discountCode.getUsers().forEach(userIntegerMap -> deleteMapForUser(userIntegerMap, discountCode));
    }

    private void deleteMapForUser(UserIntegerMap map, DiscountCode code) {
        code.getUsers().remove(map);
        discountRepository.save(code);
        userIntegerMapRepository.delete(map);
    }

    public void editDiscountCode(String code, DiscountCodeEditAttributes editAttributes)
            throws NoSuchADiscountCodeException, InvalidTimesException,
            NegativeMaxDiscountException, InvalidPercentageException {
        DiscountCode discountCode = findByCode(code);

        Date newStart = editAttributes.getStart();
        Date newEnd = editAttributes.getEnd();
        int newOffPercent = editAttributes.getOffPercent();
        int newMaxDiscount = editAttributes.getMaxDiscount();

        if (newStart != null) {
            checkIfStartingDateIsBeforeEndingDate(newStart, discountCode.getEndTime());
            discountCode.setStartTime(newStart);
        }
        if (newEnd != null) {
            checkIfStartingDateIsBeforeEndingDate(discountCode.getStartTime(), newEnd);
            discountCode.setEndTime(newEnd);
        }
        if (newOffPercent != 0) {
            checkIfPercentageIsValid(newOffPercent);
            discountCode.setOffPercentage(newOffPercent);
        }
        if (newMaxDiscount != 0) {
            checkIfMaxDiscountIsPositive(newMaxDiscount);
            discountCode.setMaxDiscount(newMaxDiscount);
        }

        discountRepository.save(discountCode);
    }

    private void checkIfNewStartingDateIsBeforeEndingDate(DiscountCode discountCode, Date newStartingDate)
            throws InvalidTimesException {
        if (newStartingDate.after(discountCode.getEndTime()))
            throw new InvalidTimesException("Starting Date Is After Ending Date");
    }

    private void checkIfStartingDateIsBeforeEndingDate(Date startDate, Date newEndingDate)
            throws InvalidTimesException {
        if (newEndingDate.before(startDate))
            throw new InvalidTimesException("Starting Date Is After Ending Date");
    }

    private void checkIfPercentageIsValid(int newPercentage) throws InvalidPercentageException {
        if (newPercentage > 100 || newPercentage < 0)
            throw new InvalidPercentageException("Invalid Percentage : (" + newPercentage + ")");
    }

    private void checkIfMaxDiscountIsPositive(long newMaxDiscount)
            throws NegativeMaxDiscountException {
        if (newMaxDiscount < 0)
            throw new NegativeMaxDiscountException("Invalid Value : " + newMaxDiscount);
    }

    public void addUserToDiscountCodeUsers(String code, Customer newUser, int timesToUse)
            throws NoSuchADiscountCodeException, UserExistedInDiscountCodeException {
        DiscountCode discountCode = findByCode(code);
        if (checkIfUserExists(newUser, discountCode))
            throw new UserExistedInDiscountCodeException("User Exist : " + newUser.getUsername());
        UserIntegerMap map = new UserIntegerMap();
        map.setDiscountCode(discountCode);
        map.setInteger(timesToUse);
        map.setUser(newUser);
        discountCode.getUsers().add(map);

        DiscountcodeIntegerMap discountcodeIntegerMap = new DiscountcodeIntegerMap();
        discountcodeIntegerMap.setDiscountCode(discountCode);
        discountcodeIntegerMap.setInteger(timesToUse);
        newUser.getDiscountCodes().add(discountcodeIntegerMap);

        userRepository.save(newUser);
        discountRepository.save(discountCode);
    }

    private boolean checkIfUserExists(User user, DiscountCode discountCode) {
        String username = user.getUsername();
        for (UserIntegerMap map : discountCode.getUsers()) {
            if (map.getUser().getUsername().equals(username))
                return true;
        }
        return false;
    }

    private UserIntegerMap findMap(User user, DiscountCode discountCode) {
        String username = user.getUsername();
        for (UserIntegerMap map : discountCode.getUsers()) {
            if (map.getUser().getUsername().equals(username))
                return map;
        }
        return null;
    }

    public void removeUserFromDiscountCodeUsers(String code, User user)
            throws NoSuchADiscountCodeException, UserNotExistedInDiscountCodeException {
        DiscountCode discountCode = findByCode(code);
        UserIntegerMap map = findMap(user, discountCode);
        if (map == null) throw new UserNotExistedInDiscountCodeException("User Is Not In Code : " + user.getUsername());
        discountCode.getUsers().remove(map);
        DiscountcodeIntegerMap mapInUser = findDiscountMap(user, code);
        Optional<Customer> customer = customerRepository.findById(user.getUsername());
        if (customer.isPresent()) {
            customer.get().getDiscountCodes().remove(mapInUser);
            customerRepository.save(customer.get());
            discountRepository.save(discountCode);
        }
    }

    public void createDiscountCode(String code, Date startTime, Date endTime, int offPercentage, long maxDiscount)
            throws AlreadyExistCodeException, InvalidTimesException, InvalidPercentageException {
        checkIfStartingDateIsBeforeEndingDate(startTime, endTime);
        checkIfPercentageIsValid(offPercentage);
        Optional<DiscountCode> discountCodeDF = discountRepository.findById(code);
        if (discountCodeDF.isPresent()) {
            throw new AlreadyExistCodeException("Code Already Exist : " + code);
        }
        DiscountCode discountCode = new DiscountCode(code, startTime, endTime, offPercentage, maxDiscount);
        discountRepository.save(discountCode);
    }

    public void useADiscount(User user, String code)
            throws NoSuchADiscountCodeException, UserNotExistedInDiscountCodeException, NoMoreDiscount {
        DiscountCode discountCode = findByCode(code);
        UserIntegerMap map = findMap(user, discountCode);
        if (map == null) throw new UserNotExistedInDiscountCodeException(user.getUsername());
        int old = map.getInteger();
        if (old == 0) {
            throw new NoMoreDiscount("You Have Used All You Codes");
        } else
            map.setInteger(old - 1);

        DiscountcodeIntegerMap discountcodeIntegerMap = findDiscountMap(user, code);
        if (discountcodeIntegerMap == null) throw new UserNotExistedInDiscountCodeException(user.getUsername());

        discountcodeIntegerMap.setInteger(old - 1);
        discountIntegerRepository.save(discountcodeIntegerMap);
    }

    private DiscountcodeIntegerMap findDiscountMap(User user, String code) {
        Optional<Customer> customer = customerRepository.findById(user.getUsername());
        if (customer.isPresent())
            for (DiscountcodeIntegerMap map : customer.get().getDiscountCodes()) {
                if (map.getDiscountCode().getCode().equals(code)) return map;
            }
        return null;
    }

    public void sysTo(String code, int amount, int numberOfUsers) {
        Random random = new Random();
        List<Customer> customers = (List<Customer>) customerRepository.findAll();
        List<Customer> randomList = new ArrayList<>();
        if (numberOfUsers < customers.size())
            for (int i = 0; i < numberOfUsers; i++) {
                int index = random.nextInt(customers.size());
                randomList.add(customers.get(index));
                customers.remove(index);
            }
        else {
            randomList.addAll(customers);
        }
        randomList.forEach(customer -> {
            try {
                addUserToDiscountCodeUsers(code, customer, amount);
            } catch (NoSuchADiscountCodeException | UserExistedInDiscountCodeException ignore) {
            }
        });
    }

    public void sysMore(String code, int amount, int i) {
        List<Customer> customers = getCustomersWithMorePurchase(i);
        customers.forEach(customer -> {
            try {
                addUserToDiscountCodeUsers(code, customer, amount);
            } catch (NoSuchADiscountCodeException | UserExistedInDiscountCodeException ignore) {
            }
        });
    }

    private List<Customer> getCustomersWithMorePurchase(int i) {
        return customerRepository.findAllByAllPurchaseIsGreaterThan(i);
    }
}
