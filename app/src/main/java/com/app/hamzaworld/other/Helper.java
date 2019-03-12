package com.app.hamzaworld.other;

import java.util.UUID;

public class Helper {

   /* //Live Key
    public static String MERCHANT_KEY ="5EEQV0T5QT";
    public static String SALT_KEY ="KBBMBEC5S5";
    public static String ENVIRONMENT ="prod";*/

    //Test Key
    public static String MERCHANT_KEY="33KM6HBG7H";
    public static String SALT_KEY="VCYMW64B95";
    public static String ENVIRONMENT ="test";

    //Unique ID
    public static String TRANSACTION_ID = UUID.randomUUID().toString();
    public static String CUSTOMER_ID = UUID.randomUUID().toString();

    public static String BASE_URL = "http://hamzaworld.com/jsons/";
    public static String IMAGE_URL = "http://hamzaworld.com/admin/uploads/";
    public static String SLIDER_URL = "http://hamzaworld.com/admin/slider/";
    public static String BANNER_URL = "http://hamzaworld.com/admin/banner/";

    public static String REGISTER_USER = "register_user.php";
    public static String LOGIN_USER = "login_user.php";
    public static String CHECK_REGISTER = "check_register.php";
    public static String UPDATE_PROFILE = "post_profile.php";
    public static String GET_PROFILE = "get_profile.php";
    public static String GET_WISHLIST = "get_wishlist.php?";
    public static String ADD_REMOVE_WISHLIST = "addremove_wishlist.php?";
    public static String REMOVE_BAG = "delete_bag.php?";
    public static String GET_CART= "get_cart.php";
    public static String ADD_QUANTITY= "add_quantity.php?";
    public static String ADD_REMOVE_CART = "addremove_cart.php";
    public static String ORDER_PRODUCT = "order_product.php?";
    public static String CHECK_ORDER = "check_order.php?";
    public static String ORDER_CONFIRMED = "order_confirmed.php?";
    public static String GET_ORDER = "order_history.php?";
    public static String GET_CATEGORY = "get_category.php?";
    public static String GET_SUBCATEGORY = "get_subcategory.php?";
    public static String GET_WISH_FLAG = "get_wish_flag.php?";
    public static String GET_CART_FLAG = "get_cart_flag.php";
    public static String GET_PRODUCT = "get_products.php";
    public static String GET_DETAIL = "get_product_detail.php";
    public static String RATING = "post_rating.php";
    public static String GET_RATING = "get_review.php";
    public static String PLACE_ORDER = "place_order.php";
    public static String GET_BANNER = "get_banner.php";
    public static String GET_BRAND = "get_brand.php";
    public static String GET_OFFER = "get_offer.php";
    public static String GET_NEW_PRODUCT = "get_home_product.php";
    public static String GET_TIMER = "get_timer.php";
    public static String GENERATE_OTP = "verify_mobile.php";
    public static String VERIFY_OTP = "verify_otp.php";
    public static String PAYMENT_DETAIL = "payment_detail.php";
    public static String GET_SIZE = "get_size.php";
    public static String ADD_REMOVE_BAG = "addremove_bag.php";
    public static String GET_COLOR_SIZE_PRODUCT = "get_color_size_product.php";

    public static String cart="0";
    public static String bag="0";
}
