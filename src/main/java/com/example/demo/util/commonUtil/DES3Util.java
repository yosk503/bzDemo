package com.example.demo.util.commonUtil;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;

public class DES3Util {
	/**  
     * @param args在java中调用sun公司提供的3DES加密解密算法时，需要使  
     * 用到$JAVA_HOME/jre/lib/目录下如下的4个jar包：  
     *jce.jar  
     *security/US_export_policy.jar  
     *security/local_policy.jar  
     *ext/sunjce_provider.jar   
     */

	private  static  final Log logger= LogFactory.getLog(DES3Util.class);

	private static final String Algorithm = "DESede"; //定义加密算法,可用 DES,DESede,Blowfish
    //keybyte为加密密钥，长度为24字节      
    //src为被加密的数据缓冲区（源）  
    public static byte[] encryptMode(byte[] keybyte,byte[] src){  
         try {  
            //生成密钥  
            SecretKey deskey = new SecretKeySpec(keybyte, "DESede");  
            System.out.println("生成秘钥Key："+deskey);
            //加密  
            Cipher c1 = Cipher.getInstance(Algorithm);  
            c1.init(Cipher.ENCRYPT_MODE, deskey);  
            return c1.doFinal(src);//在单一方面的加密或解密  
        } catch (java.security.NoSuchAlgorithmException e1) {  
             e1.printStackTrace();
			 logger.error("找不到加密算法:"+Algorithm,e1);
        }catch(javax.crypto.NoSuchPaddingException e2){  
             e2.printStackTrace();
			 logger.error("加密出错",e2);
        }catch(Exception e3){
             e3.printStackTrace();
			 logger.error("加密出错",e3);
        }  
        return null;  
    }  
      
    //keybyte为加密密钥，长度为24字节      
    //src为加密后的缓冲区  
    public static String decryptMode(byte[] keybyte,byte[] src){  
        try {  
            //生成密钥  
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);  
            //解密  
            System.out.println("生成秘钥Key："+deskey);
            Cipher c1 = Cipher.getInstance(Algorithm);  
            c1.init(Cipher.DECRYPT_MODE, deskey);  
            byte[] desByte =  c1.doFinal(src);
            String desStr = new String(desByte ,"UTF-8");
            return desStr;
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
			logger.error("找不到加密算法:"+Algorithm,e1);
        }catch(javax.crypto.NoSuchPaddingException e2){  
            e2.printStackTrace();
			logger.error("加密出错",e2);
        }catch(Exception e3){
            e3.printStackTrace();
			logger.error("加密出错",e3);
        }  
        return null;          
    }  
      
    //转换成十六进制字符串  
    public static String byte2Hex(byte[] b){  
        String hs="";  
        String stmp="";  
        for(int n=0; n<b.length; n++){  
            stmp = (Integer.toHexString(b[n]& 0XFF));
            if(stmp.length()==1){  
                hs = hs + "0" + stmp;                 
            }else{  
                hs = hs + stmp;  
            }  
            if(n<b.length-1) hs=hs+":";
        }  
        return hs.toUpperCase();          
    }  
     

    /** 
         * @Title:string2HexString 
         * @Description:字符串转16进制字符串 
         * @param strPart 
         *            字符串 
         * @return 16进制字符串 
         * @throws 
         */  
        public static String string2HexString(String strPart) {  
            StringBuffer hexString = new StringBuffer();  
           for (int i = 0; i < strPart.length(); i++) {  
                int ch = (int) strPart.charAt(i);  
                String strHex = Integer.toHexString(ch);  
                hexString.append(strHex);  
            }  
            return hexString.toString();  
        }  

	//BASE64编码
	public static String encodeBase64(byte[] src_byte) {
		BASE64Encoder base64Encoder = new BASE64Encoder();
		try {
			// 经过BASE64加密后的密文
			String base64String = base64Encoder.encodeBuffer(src_byte);
			return base64String;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("BASE64编码出错",e);
			return null;
		}
	}

	// base64解码
	public byte[] decodeBase64(String base64_string) {
		BASE64Decoder base64Decoder = new BASE64Decoder();
		try {
			// 将BASE64转码过的字符串进行解码,获取明文
			byte[] src_byte = base64Decoder.decodeBuffer(base64_string);
			return src_byte;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("BASE64解码出错",e);
			return null;
		}

	}
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
     * 十六进制字符串转化成字节数组
	 * @author gql
	 * @param s 十六进制字符串
	 * @return by 密文字节数组
	 * */
	public static byte[] String2byte(String s) {
    	byte[] by = new byte[s.length()/2];
    	for(int i=0;i<s.length();i+=2){
    		Integer value = Integer.parseInt(s.substring(i, i+2), 16);
    		by[i/2] = value.byteValue();
    	}
        return by;
    }

	 /**
     * 字节数组转化成十六进制字符串
	 * @author gql
	 * @param b 接受一个字节数组
	 * @return hexString 表示字节数组的十六进制形式的字符串
	 * */
	public static String byte2String(byte[] b) {
        String hexString="";
        String stmp="";

        for (int n=0;n<b.length;n++) {
            stmp=(Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1) 
            	hexString=hexString+"0"+stmp;
            else 
            	hexString=hexString+stmp;
        }
        return hexString.toUpperCase();
    }

	 
	 /**
	  * @param src
	  * @param key
	  * @return 加密操作:DES加密后的字节数组，通过base64加密操作，再转16进制字符串
	  * @throws Exception
	  */

	 public static String encrypt(String src, String key) throws Exception
	    {
	    	DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
	    	SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
	    	SecretKey securekey = keyFactory.generateSecret(dks);
	    	
	    	Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
	    	cipher.init(Cipher.ENCRYPT_MODE, securekey);
	    	byte[] b = cipher.doFinal(src.getBytes("UTF-8"));
	    	BASE64Encoder encoder = new BASE64Encoder();
	    	return encode(encoder.encode(b).replaceAll("\r", "").replaceAll("\n", ""));
	    }




	    
	  /**
	   * @param src
	   * @param key
	   * @return 解密操作：16进制解码，Base64解码，DES解密
	   * @throws Exception
	   */
	    public static String decryptThreeDESECB(String src,String key) throws Exception {
	    	src = decode(src);
	        //--通过base64,将字符串转成byte数组  
	        BASE64Decoder decoder = new BASE64Decoder();  
	        byte[] bytesrc = decoder.decodeBuffer(src);
	        //--解密的key  
	        DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));  
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");  
	        SecretKey securekey = keyFactory.generateSecret(dks);  
	          
	        //--Chipher对象解密  
	        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");  
	        cipher.init(Cipher.DECRYPT_MODE, securekey);  
	        byte[] retByte = cipher.doFinal(bytesrc);  
	          
	        return new String(retByte,"UTF-8");
	    }

	    
	    /* 
	    * 将字符串编码成16进制数字,适用于所有字符（包括中文） 
	    */ 
	    public static String encode(String str){
			try {
				String hexString = "0123456789ABCDEF";
				//根据默认编码获取字节数组
				byte[] bytes = str.getBytes("UTF-8");
				StringBuilder sb = new StringBuilder(bytes.length * 2);
				//将字节数组中每个字节拆解成2位16进制整数
				for (int i = 0; i < bytes.length; i++) {
					sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
					sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
				}
				return sb.toString();
			}catch(Exception e){
				e.printStackTrace();
				logger.error("16进制编码出错",e);
			}
			return null;
		} 
	    /* 
	    * 将16进制数字解码成字符串,适用于所有字符（包括中文） 
	    */ 
	    public static String decode(String bytes) {
          try {
			  String hexString = "0123456789ABCDEF";
			  ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
			  //将每2位16进制整数组装成一个字节
			  for (int i = 0; i < bytes.length(); i += 2)
				  baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
			  return new String(baos.toByteArray(), "UTF-8");
		  }catch(Exception e){
			  e.printStackTrace();
			  logger.error("16进制解码出错",e);
		  }
			return null;
	    }


	public static void main(String[] args) {
		try {
			//添加新安全算法,如果用JCE就要把它添加进去
//			Security.addProvider(new com.sun.crypto.provider.SunJCE());
//			final byte[] keyBytes =String2byte(string2HexString("123456789000000000000000"));
//			String a = byte2String(keyBytes);
//			String szSrc = "customNo=w23e3242e323234234w23e3242e323234234&custName=张阿斯顿发三";
//			System.out.println("加密前的字符串:" + szSrc);
//			byte[] encoded = encryptMode(keyBytes,szSrc.getBytes("UTF-8"));
//			System.out.println("加密后的字符串:" +new String(encoded,"UTF-8"));
//			System.out.println("加密后16进制的字符串:" + DES3Util.byte2String(encoded));
//			String str = byte2String(encoded);
//			// String str = "996DB929CA19C0A2F213A5A06A2B7FA8C835C79AD3884D41BC3AFD601536D8685C8FB3B6D632C261C87F775FA8D871A31B827C8B606BFDF976CF6C9D1371A404FA799C4F76C8BDF700F7589FB35E9A1F";
//			encoded = String2byte(str);
//			String dessrc = decryptMode(keyBytes,encoded);
//			System.out.println("解密后的字符串:" + dessrc);
//			String src = "customNo=w23e3242e323234234w23e3242e323234234&custName=张阿斯顿发三";
//			String key = "123456789000000000000000";
//			//加密
//			String target=encrypt(src,key);
//			System.out.println("加密前数据："+src);
//			System.out.println("加密后数据："+target);
//			str = encode(encrypt(src,key));
//			System.out.println("加密后16进制数据："+str);
//			str =decode(str);
//			System.out.println("得到的16进制数据："+str);
//			System.out.println("解密后数据：" + decryptThreeDESECB(target,key));
			encrypt("aA13689276331!","testShiroEncryptKeyYangLongGui");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
