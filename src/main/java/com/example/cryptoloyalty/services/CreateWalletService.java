package com.example.cryptoloyalty.services;
import com.clickhouse.client.internal.google.gson.Gson;
import com.example.cryptoloyalty.entity.WalletEntity;
import com.example.cryptoloyalty.repositories.WalletRepository;
import com.example.cryptoloyalty.util.Constants;
import com.example.cryptoloyalty.util.Issuancereq;
import com.example.wallet.contracts.OCCoin;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CreateWalletService {

    private final WalletRepository repo;
    private final KeyEncryptionService crypto;
    private final ERC20Client erc20;
    private final OCCoinClient ocCoinClient;
    private String privateKeyHex;
    private final Logger logger= LoggerFactory.getLogger(CreateWalletService.class);
    private static final String CONTRACT = "0x6f6212601cce704a81B201D9Fe0E6CBEc1A3C194";


    @Autowired
    private Web3j web3j;





    public CreateWalletService(
            WalletRepository repo,
            KeyEncryptionService crypto,
            ERC20Client erc20, OCCoinClient ocCoinClient
    ) {
        this.repo = repo;
        this.crypto = crypto;
        this.erc20 = erc20;
        this.ocCoinClient = ocCoinClient;
    }

    // CREATE WALLET
    public ResponseEntity createWallet(String userId) throws Exception {
       logger.info("userId : {}",userId);
        Credentials creds= Credentials.create(getPrivateKeyHex());
        WalletEntity wallet = new WalletEntity();
        wallet.setUserId(userId);
        wallet.setWalletAddress(creds.getAddress());
        logger.info("Address ::{}",creds.getAddress());
        wallet.setStatus("Active");
        wallet.setEncryptedPrivateKey(getPrivateKeyHex());
        wallet.setCreatedAt(LocalDateTime.now());
        wallet=repo.save(wallet);
        Map<String, String> response= new HashMap<>();
        response.put("userId",userId);
        response.put("walletAddress", wallet.getWalletAddress());
        response.put("status", Constants.CREATED);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    // TRANSFER TOKENS
    public String transfer(
            String fromUserId,
            String toAddress,
            BigDecimal amountTokens,
            String transferType
            ) throws Exception {

        BigInteger wei = amountTokens
                .multiply(BigDecimal.TEN.pow(18))
                .toBigIntegerExact();

        WalletEntity wallet =
                repo.findByUserId(fromUserId).orElseThrow();
        logger.info("toAddress==>"+toAddress);
        WalletEntity toWallet =
                repo.findByWalletAddress(toAddress.toLowerCase()).orElseThrow();


        String privateKey = wallet.getEncryptedPrivateKey();
        TransactionReceipt receipt = null;
        if(transferType.equals(Constants.ISSUANCE)){
            //issuance as 10% of receipt amount
            BigInteger coinsToBeSend= (wei.multiply(BigInteger.TEN)).divide(BigInteger.valueOf(100));
            receipt =
                    erc20.transfer(privateKey, toAddress, coinsToBeSend);
            boolean issuanceDone= updateIssuanceInOC(toWallet.getMembershipNumber(),amountTokens.doubleValue());
            if(issuanceDone){
                logger.info("Issuance completed successfully !");
            }
        }
        else if(transferType.equals(Constants.REDEMPTION)){
            //redemption
            receipt = erc20.transfer(privateKey, toAddress, wei);
            boolean redemtionDone=updateRedemptionInOC(wallet.getMembershipNumber(), wei.multiply(BigInteger.TEN).doubleValue());
        }

        return receipt.getTransactionHash();
    }
    private boolean updateRedemptionInOC(String membershipNumber,Double amountToBeRedeemed) {
        return  true;
    }
    public String getPrivateKeyHex() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
//        if(Constants.priavteKey==null) {
            ECKeyPair masterKey = Keys.createEcKeyPair();
            String masterPrivateKeyHex = masterKey.getPrivateKey().toString(16);
            System.out.println(masterPrivateKeyHex);
            Constants.priavteKey =masterPrivateKeyHex;
            logger.info("private Key: {}",masterPrivateKeyHex);
//        }
        return Constants.priavteKey;
    }
    public boolean updateIssuanceInOC(String membershipNum,Double enteredAmount) throws JsonProcessingException {
        Issuancereq request = new Issuancereq();
        // Header
        Issuancereq.Header header = new Issuancereq.Header();
        String timeStamp= System.nanoTime()+"";
        header.setRequestId(timeStamp);
        header.setRequestDate("2025-12-23 18:00:00");
        header.setSubsidiaryNumber("14");
        header.setStoreNumber("14");
        header.setDocSID("OC_"+timeStamp);
        header.setReceiptNumber("POS/amar/"+timeStamp);

        // Membership
        Issuancereq.Membership membership = new Issuancereq.Membership();
        logger.info("membership ==> "+ membership);
        logger.info("membership ==> "+ membershipNum);
        membership.setCardNumber("");
        membership.setPhoneNumber(membershipNum);

        // Amount
        Issuancereq.Amount amount = new Issuancereq.Amount();
        amount.setType("Purchase");
        amount.setEnteredValue(enteredAmount+"");
        amount.setValueCode("Currency");
        amount.setReceiptAmount(enteredAmount+"");
        amount.setReturnedAmount("0.0");
        amount.setPurchaseValue(enteredAmount+"");
        amount.setIncludeRedeemAmount(enteredAmount+"");

        // Promotions
        Issuancereq.Discounts.Promotion promo = new Issuancereq.Discounts.Promotion();
        promo.setName("");

        Issuancereq.Discounts discounts = new Issuancereq.Discounts();
        discounts.setAppliedPromotion("NA");
        discounts.setPromotions(List.of(promo));

        // Items
        Issuancereq.Item item1 = getItem();

        // (Repeat for item2, item3 exactly same way)

        // Customer
        Issuancereq.Customer customer = new Issuancereq.Customer();
        customer.setCustomerId("");
        customer.setFirstName("amar");
        customer.setLastName("oc");
        customer.setPhone(membershipNum);
        customer.setEmailAddress("amarnath@optculture.com");
        customer.setAddressLine1("");
        customer.setCity("");
        customer.setState("");
        customer.setPostal("");
        customer.setBirthday("");
        customer.setAnniversary("");

        // User
        Issuancereq.User user = new Issuancereq.User();
        user.setUserName("Ginesys");
        user.setOrganizationId("Ginesys");
        user.setToken("QA7R7AG5BA266W51");

        // Assemble
        request.setHeader(header);
        request.setMembership(membership);
        request.setAmount(amount);
        request.setDiscounts(discounts);
        request.setItems(List.of(item1 /*, item2, item3 */));
        request.setCustomer(customer);
        request.setUser(user);
        Gson gson = new Gson();
        //Convert Object to JSON string
        String requestJson = gson.toJson(request);
        logger.info("RequestJson::::"+requestJson);
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Set content type to JSON

        // Create the HTTP entity with the request body and headers
        HttpEntity<Issuancereq> entity = new HttpEntity<>(request, headers);

        // Initialize RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Define the API URL
        String apiUrl =  "https://qcapp.optculture.com/subscriber/OCLoyaltyIssuance.mqrm";

        // Send the POST request
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiUrl,
                    entity,
                    String.class
            );

            // Handle the response
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Response: " + response.getBody());
                if (response.getBody().contains("status\":{\"errorCode\":\"0\"")) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.info("Exception while issuance ",e);
        }
        return false;
    }

    public TransactionReceipt mint(
            String toAddress,
            BigInteger amount
    ) throws Exception {

        return ocCoinClient.adminMint(Constants.priavteKey,
                toAddress,
                amount);
    }

    public TransactionReceipt mine(String toAddress) throws Exception {
        WalletEntity toWallet =
                repo.findByWalletAddress(toAddress.toLowerCase()).orElseThrow();

        Credentials creds = Credentials.create(toWallet.getEncryptedPrivateKey());

        OCCoin coin = OCCoin.load(
                CONTRACT,
                web3j,
                creds,
                new DefaultGasProvider()
        );

        return coin.mine().send();
    }



    private static Issuancereq.@NonNull Item getItem() {
        Issuancereq.Item item1 = new Issuancereq.Item();
        item1.setItemCategory("Women Tops");
        item1.setDepartmentCode("");
        item1.setItemClass("100% Cotton, 599, 62043200 Sleeveless Fitted Party Crop Top XS / Black");
        item1.setItemSubClass("");
        item1.setDCS("STYLE UNION YWTO00257 CREAM XL SU-YWTO00257-125 SS25");
        item1.setVendorCode("");
        item1.setSkuNumber("8909001653395");
        item1.setBilledUnitPrice("1529.10");
        item1.setOriginalUnitPrice("1699.0");
        item1.setQuantity("1.0");
        item1.setTax("0.00");
        item1.setDiscount("169.90");
        item1.setDepartmentName("YW TOPWEAR");
        item1.setItemClassName("");
        item1.setItemSubClassName("");
        item1.setVendorName("");
        item1.setItemSID("46682546569465");
        item1.setItemNote("");
        return item1;
    }

}
