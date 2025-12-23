package com.example.cryptoloyalty.util;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
public class Issuancereq {
        private Header header;
        private Membership membership;
        private Amount amount;
        private Discounts discounts;
        private List<Item> items;
        private Customer customer;
        private User user;

        @Data
        public static class Header {
            private String requestId;
            private String requestDate;
            private String subsidiaryNumber;
            private String storeNumber;
            private String docSID;
            private String receiptNumber;

        }
        @Data
        public static class Membership {
            private String cardNumber;
            private String phoneNumber;
        }
        @Data
        public static class Amount {
            private String type;
            private String enteredValue;
            private String valueCode;
            private String receiptAmount;
            private String returnedAmount;
            private String purchaseValue;
            private String includeRedeemAmount;

        }

        @Getter
        @Setter
        public static class Discounts {
            private String appliedPromotion;
            private List<Promotion> promotions;

            @Data
            public static class Promotion {
                private String name;
            }
        }
        @Data
        public static class Item {
            private String itemCategory;
            private String departmentCode;
            private String itemClass;
            private String itemSubClass;
            private String DCS;
            private String vendorCode;
            private String skuNumber;
            private String billedUnitPrice;
            private String originalUnitPrice;
            private String quantity;
            private String tax;
            private String discount;
            private String departmentName;
            private String itemClassName;
            private String itemSubClassName;
            private String vendorName;
            private String itemSID;
            private String itemNote;
        }
        @Data
        public static class Customer {
            private String customerId;
            private String firstName;
            private String lastName;
            private String phone;
            private String emailAddress;
            private String addressLine1;
            private String city;
            private String state;
            private String postal;
            private String birthday;
            private String anniversary;
        }
        @Data
        public static class User {
            private String userName;
            private String organizationId;
            private String token;
        }
}

