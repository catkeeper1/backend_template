import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;

import java.io.*;

public class OssTest {

    public static void main(String[] args) throws IOException {
        String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
        String accessKeyId = "LTAI4G2GgWdjprR2RhyaMNN8";
        String accessKeySecret = "QJCksiJ9MGCFc9SkVOjyY7EU7H2unj";


        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        File file = new File("F:\\DEV TOOLS and DOC\\doc\\dojo\\docs-master\\DojoToolboxBuilder_djConfig.jpg");
        FileInputStream fileInputStream = new FileInputStream(file);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024 * 4];
        int len;
        while ((len = fileInputStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, len);
        }

        byte[] content = byteArrayOutputStream.toByteArray();

        String type = getPicType(content);
        System.out.println("type = " + type);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/" + type);
        ossClient.putObject("ckr-test-bucket",
                "sgs-user/test-file1",
                new ByteArrayInputStream(content),
                objectMetadata);

        ossClient.shutdown();
        byteArrayOutputStream.close();
        fileInputStream.close();
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static final String TYPE_JPG = "jpg";
    public static final String TYPE_GIF = "gif";
    public static final String TYPE_PNG = "png";
    public static final String TYPE_BMP = "bmp";
    public static final String TYPE_WEBP = "webp";
    public static final String TYPE_TIF = "tif";


    public static String getPicType(byte[] fileContent) {
        //读取文件的前几个字节来判断图片格式
        byte[] b = new byte[4];


        System.arraycopy(fileContent, 0, b, 0, b.length);

        String type = bytesToHexString(b).toUpperCase();
        if (type.contains("FFD8FF")) {
            return TYPE_JPG;
        } else if (type.contains("89504E47")) {
            return TYPE_PNG;
        } else if (type.contains("47494638")) {
            return TYPE_GIF;
        } else if (type.contains("424D")) {
            return TYPE_BMP;
        } else if (type.contains("52494646")) {
            return TYPE_WEBP;
        } else if (type.contains("49492A00")) {
            return TYPE_TIF;
        } else {
            return TYPE_JPG;
        }


    }


}
