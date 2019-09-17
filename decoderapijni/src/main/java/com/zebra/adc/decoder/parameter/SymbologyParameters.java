package com.zebra.adc.decoder.parameter;

/**
 * @author pang
 * @CreateDate 2019/1/14
 * @UpdateUser Huang
 * @UpdateDate 2019/1/18 9:42
 * 码制设置时所用到的码制参数值
 */
public class SymbologyParameters {

///////////// UPC/EAN ///////////////////////////
    /**UPC-A default enable*/
    public static final int UPC_A = 1 ;
    /**UPC-E default enable*/
    public static final int UPC_E = 2 ;
    /**UPC-E1 default disable*/
    public static final int UPC_E1 = 12 ;
    /**EAN-8/JAN8 default enable*/
    public static final int EAN_8 = 4 ;
    /**EAN-13 default enable*/
    public static final int EAN_13 = 3 ;
    /**BOOKLAND_EAN default disable*/
    public static final int BOOKLAND_EAN = 83 ;
    /**UPC_EAN_JAN_2_5_DIGITS default ignore*/
    public static final int UPC_EAN_JAN_2_5_DIGITS = 16 ;
    /**UPC/EAN/JAN Supplemental Redundancy default 10*/
    public static final int UPC_EAN_JAN_SUP_REDUNDANCY = 80 ;
    /**Decode UPC/EAN/JAN Supplemental AIM ID default combined*/
    public static final int UPC_EAN_JAN_SUP_AIM_ID = 672 ;
    /**UPC Reduced Quiet Zone default disable*/
    public static final int UPC_REDUCED_QUIET_ZONE = 1289 ;
    /**Transmit UPC-A Check Digit default enable*/
    public static final int UPC_A_TRANSMIT_CHECK_DIGIT = 40 ;
    /**Transmit UPC-E Check Digit   default enable*/
    public static final int UPC_E_TRANSMIT_CHECK_DIGIT = 41 ;
    /**Transmit UPC-E1 Check Digit default enable*/
    public static final int UPC_E1_TRANSMIT_CHECK_DIGIT = 42 ;
    /**UPC-A Preamble default System Character*/
    public static final int UPC_A_PREAMBLE = 34 ;
    /**UPC-E Preamble default System Character*/
    public static final int UPC_E_PREAMBLE = 35 ;
    /**UPC-E1 Preamble default System Character*/
    public static final int UPC_E1_PREAMBLE = 36 ;
    /**Convert UPC-E to A default disable*/
    public static final int UPC_E_TO_A = 37 ;
    /**Convert UPC-E1 to A default disable*/
    public static final int UPC_E1_TO_A = 38 ;
    /**EAN-8/JAN-8 Extend  default disable*/
    public static final int EAN_8_EXTEND = 39 ;
    /**Bookland ISBN Format  default ISBN-10*/
    public static final int BOOKLAND_ISBN_FORMAT = 576 ;
    /**UCC Coupon Extended Code  default enable*/
    public static final int UCC_COUPON_EXTEND = 85 ;
    /**ISSN EAN  default disable*/
    public static final int ISSN_EAN = 617 ;

    ///////////CODE 128/////////////
    /**Code 128 default enable*/
    public static final int CODE_128 = 8 ;
    /**Set Lengths for Code 128 */
    public static final int CODE_128_SET_LENGTH1 = 209 ;
    public static final int CODE_128_SET_LENGTH2 = 210 ;
    /**GS1-128 (formerly UCC/EAN-128)  default enable*/
    public static final int GS1_128 = 14 ;
    /**ISBT 128  default enable*/
    public static final int ISBT_128  = 84 ;
    /**ISBT Concatenation default disable*/
    public static final int ISBT_CONCATENATION = 577 ;
    /**Check ISBT Table default enable*/
    public static final int ISBT_TABLE_CHECK = 578 ;
    /**ISBT Concatenation Redundancy  default 10*/
    public static final int ISBT_CONCATENATION_REDUNDANCY = 223 ;
    /**Code 128 Reduced Quiet Zone default disable*/
    public static final int CODE_128_REDUCED_QUIET_ZONE = 1208 ;
    /**Ignore Code 128 <FNC4> default enable*/
    public static final int IGNORE_CODE_128 = 1254 ;

    ////////////CODE 39///////////////////
    /**Code 39 default enable*/
    public static final int CODE_39 = 0 ;
    /**Trioptic Code 39 default disable*/
    public static final int TRIOPTIC_CODE_39 = 13 ;
    /**Convert Code 39 to Code 32 default DISABLE*/
    public static final int CONVERT_CODE_39_TO_CODE_32 = 86 ;
    /**Code 32 Prefix default DISABLE*/
    public static final int CODE_32_PREFIX = 231 ;
    /**Set Length(s) for Code 39 default 2 TO 55*/
    public static final int CODE_39_SET_LENGTH1 = 18 ;
    public static final int CODE_39_SET_LENGTH2 = 19 ;
    /**Code 39 Check Digit Verification default DISABLE*/
    public static final int CODE_39_CHECK_DIGIT_VERIFICATION = 48 ;
    /**Transmit Code 39 Check Digit default DISABLE*/
    public static final int TRANSMIT_CODE_39 = 43 ;
    /**Code 39 Full ASCII Conversion default DISABLE*/
    public static final int CODE_39_FULL_ASCII_CONVERSION = 17 ;
    /**Code 39 Reduced Quiet Zone default DISABLE*/
    public static final int CODE_39_REDUCED_QUIET_ZONE = 1209 ;

    ////////////CODE 93///////////////////
    /**Code 93 default enable*/
    public static final int CODE_93 = 9 ;
    /**Set Length(s) for Code 93 default 4 TO 55*/
    public static final int CODE_93_SET_LENGTHS1 = 26 ;
    public static final int CODE_93_SET_LENGTHS2 = 27 ;

    ////////////CODE 11///////////////////
    /**Code 11 default enable*/
    public static final int CODE_11 = 10 ;
    /**Set Lengths for Code 11 default  4 TO 55*/
    public static final int CODE_11_SET_LENGTHS1 = 28 ;
    public static final int CODE_11_SET_LENGTHS2 = 29 ;
    /**Code 11 Check Digit Verification default DISABLE*/
    public static final int CODE_11_CHECK_DIGIT_VERIFICATION = 52 ;
    /**Transmit Code 11 Check Digit(s) default enable*/
    public static final int TRANSMIT_CODE_11_CHECK_DIGIT = 47 ;

    ////////////Interleaved 2 of 5 (ITF)///////////////////
    /**Interleaved 2 of 5 (ITF) default enable*/
    public static final int I_2_OF_5 = 6 ;
    /**Set Lengths for I 2 of 5 default 14*/
    public static final int I_2_OF_5_SET_LENGTHS1 = 22 ;
    public static final int I_2_OF_5_SET_LENGTHS2 = 23 ;
    /**I 2 of 5 Check Digit Verification  default DISABLE*/
    public static final int I_2_OF_5_CHECK_DIGIT_VERIFICATION = 49 ;
    /**Transmit I 2 of 5 Check Digit  default DISABLE*/
    public static final int TRANSMIT_I_2_OF_5_CHECK_DIGIT = 44 ;
    /**Convert I 2 of 5 to EAN 13 default DISABLE*/
    public static final int CONVERT_I_2_OF_5_TO_EAN13 = 82 ;
    /**I 2 of 5 Security Level default Security Level 1*/
    public static final int I_2_OF_5_SECURITY_LEVEL = 1121 ;
    /**I 2 of 5 Reduced Quiet Zone default DISABLE*/
    public static final int I_2_OF_5_REDUCED_QUIET_ZONE = 1210 ;

    /////////////////Discrete 2 of 5 (DTF)/////////////////////////////
    /**Discrete 2 of 5 (DTF) default enable*/
    public static final int D_2_OF_5 = 5 ;
    /**Set Length(s) for D 2 of 5 default enable*/
    public static final int D_2_OF_5_LENGTHS1 = 20 ;
    public static final int D_2_OF_5_LENGTHS2 = 21 ;

    /////////////////Codabar (NW - 7)///////////////////////////////
    /**Codabar default DISABLE*/
    public static final int CODABAR = 7 ;
    /**Set Lengths for Codabar  default 5-55*/
    public static final int CODABAR_LENGTHS1 = 24 ;
    public static final int CODABAR_LENGTHS2 = 25 ;
    /**CLSI Editing default DISABLE*/
    public static final int CODABAR_CLSI = 54 ;
    /**NOTIS Editing default DISABLE*/
    public static final int CODABAR_NOTIS = 55 ;

    ///////////////MSI //////////////////////////////////////////////
    /**MSI default DISABLE*/
    public static final int MSI = 11 ;
    /**Set Length(s) for MSI  default 4-55*/
    public static final int MSI_SET_LENGTHS1 = 30 ;
    public static final int MSI_SET_LENGTHS2 = 31 ;
    /**MSI Check Digits default ONE*/
    public static final int MSI_CHECK_DIGITS = 50 ;
    /**Transmit MSI Check Digit default DISABLE*/
    public static final int TRANSMIT_MSI_CHECK_DIGIT = 46 ;
    /**MSI Check Digit Algorithm default Mod 10/Mod 10*/
    public static final int MSI_CHECK_DIGIT_ALGORITHM = 51 ;

    ///////////////Chinese 2 of 5 ////////////////////////////////////
    /**Chinese 2 of 5 default DISABLE*/
    public static final int CHINESE_2_OF_5 = 408 ;

    ///////////////Korean 3 of 5 ////////////////////////////////////
    /**Korean 3 of 5 default DISABLE*/
    public static final int KOREAN_3_OF_5 = 581 ;

    ///////////////Matrix 2 of 5 ////////////////////////////////////
    /**Matrix 2 of 5 default DISABLE*/
    public static final int MATRIX_2_OF_5 = 618 ;
    /**Set Length(s) for Matrix 2 of 5  default 14*/
    public static final int MATRIX_2_OF_5_SET_LENGTHS1 = 619 ;
    public static final int MATRIX_2_OF_5_SET_LENGTHS2 = 620 ;
    /**Matrix 2 of 5 Redundancy default DISABLE*/
    public static final int MATRIX_2_OF_5_REDUNDANCY = 621 ;
    /**Matrix 2 of 5 Check Digit default DISABLE*/
    public static final int MATRIX_2_OF_5_CHECK_DIGIT = 622 ;
    /**Transmit Matrix 2 of 5 Check Digit default DISABLE*/
    public static final int TRANSMIT_MATRIX_2_OF_5_CHECK_DIGIT = 623 ;
    /**Inverse 1D  default Regular*/
    public static final int INVERSE_1D = 586 ;

    ///////////////Postal Codes /////////////////////////////////////

    /**US Postnet  default enable*/
    public static final int US_POSTNET = 89 ;
    /**US Planet default enable*/
    public static final int US_PLANET = 90 ;
    /**Transmit US Postal Check Digit default enable*/
    public static final int TRANSMIT_US_POSTAL_CHECK_DIGIT = 95 ;
    /**UK Postal  default enable*/
    public static final int UK_POSTAL = 91 ;
    /**Transmit UK Postal Check Digit default enable*/
    public static final int TRANSMIT_UK_POSTAL_CHECK_DIGIT = 96 ;
    /**Japan Postal  default enable*/
    public static final int JAPAN_POSTAL = 290 ;
    /**Australia Post  default enable*/
    public static final int AUSTRALIA_POST = 291 ;
    /**Australia Post Format default Autodiscriminate*/
    public static final int AUSTRALIA_POST_FORMAT = 718 ;
    /**Netherlands KIX Code  default enable*/
    public static final int NETHERLANDS_KIX_CODE = 326 ;
    /**USPS 4CB/One Code/Intelligent Mai default DISABLE*/
    public static final int USPS_4CB = 592 ;
    /**UPU FICS Postal  default DISABLE*/
    public static final int UPU_FICS_POSTAL = 611 ;

    ///////////////GS1 DataBar (formerly RSS, Reduced Space Symbology) /////////////////////////////
    /**GS1 DataBar-14  default enable*/
    public static final int GS1_DATABAR_14 = 338 ;
    /**GS1 DataBar Limited default DISABLE*/
    public static final int GS1_DATABAR_LINITED = 339 ;
    /**GS1 DataBar Limited Security Level  default 3*/
    public static final int GS1_DATABAR_LINITED_SECURITY_LEVEL = 728 ;
    /**GS1 DataBar Expanded default DISABLE*/
    public static final int GS1_DATABAR_EXPANDED = 340 ;
    /**Convert GS1 DataBar to UPC/EAN default DISABLE*/
    public static final int CONVERT_GS1_DATABAR_TO_UPC_EAN = 397 ;

    /////////////// Composite //////////////////////////////////////////////////////////////////////
    /**Composite CC-C default DISABLE*/
    public static final int COMPOSITE_CC_C = 341 ;
    /**Composite CC-A/B default DISABLE*/
    public static final int COMPOSITE_CC_A_B = 342 ;
    /**Composite TLC-39 default Disable*/
    public static final int COMPOSITE_TLC_39 = 371 ;
    /**UPC Composite Mode  default Never Linked*/
    public static final int UPC_COMPOSITE_MODE = 344 ;
    /**GS1-128 Emulation Mode for UCC/EAN Composite Codes default Disable*/
    public static final int GS1_128_EMULATION_MODE_FOR_UCC_EAN_COMPOSITE = 427 ;

    //2D Symbologies
    /////////////// PDF417 //////////////////////////////////////////////////////////////////////
    /**PDF417 default enable*/
    public static final int PDF417 = 15 ;
    /**MicroPDF417 default Disable*/
    public static final int MICROPDF417 = 227 ;
    /**Code 128 Emulation default Disable*/
    public static final int CODE_128_EMULATION = 123 ;
    /**Data Matrix default enable*/
    public static final int DATA_MATRIX = 292 ;
    /**Data Matrix Inverse  default Regular*/
    public static final int DATA_MATRIX_INVERSE = 588 ;
    /**Decode Mirror Images (Data Matrix Only)  default Never*/
    public static final int DECODE_MIRROR_IMAGES = 537 ;
    /**Maxicode default enable*/
    public static final int MAXICODE = 294 ;
    /**QR Code default enable*/
    public static final int QR_CODE = 293 ;
    /**QR Inverse default Regular*/
    public static final int QR_INVERSE = 587 ;
    /**MicroQR default enable*/
    public static final int MICRO_QR = 573 ;
    /**Aztec default enable*/
    public static final int AZTEC = 574 ;
    /**Aztec Inverse  default Regular*/
    public static final int AZTEC_INVERSE = 589 ;
    /**Han Xin default Disable*/
    public static final int HAN_XIN = 1167 ;
    /**Han Xin Inverse  default Regular*/
    public static final int HAN_XIN_INVERSE = 1168 ;

    ///Symbology-Specific Security Levels
    /**Redundancy Level default 1 */
    public static final int REDUNDANCY = 78 ;
    /**Security Level  default 1*/
    public static final int SECURITY_LEVEL = 77 ;
    /**1D Quiet Zone Level default 1*/
    public static final int QUIET_ZONE_LEVEL_1D = 1288 ;
    /**Intercharacter Gap Size default enable*/
    public static final int INTERCHARACTER_GAP_SIZE = 381 ;


}
