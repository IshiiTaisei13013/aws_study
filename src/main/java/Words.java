//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.GetObjectRequest;
//import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

import java.io.*;
import java.util.Map;

public class Words implements RequestHandler<Map<String,String>, String> {

    static final String S3_ACCESS_KEY = System.getenv("ACCESS_KEY");
    static final String S3_SECRET_KEY = System.getenv("SECRET_KEY");
    static final String S3_BUCKET_NAME = System.getenv("S3_BUCKET_NAME");
    static final String S3_BUCKET_KEY = System.getenv("S3_BUCKET_KEY");

    public AmazonS3 authS3Client() {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .build();
        return s3Client;
    }

//    public void upload(){
//        System.out.println("upload start");
//
//        //クライアント認証
//        AmazonS3 S3Client = authS3Client();
//
//        //ファイル読み込み
//        File file = new File(DESKTOP_PATH + "//upload.json");
//        FileInputStream fis = new FileInputStream(file);
//
//        //ファイルの長さを取得
//        ObjectMetadata om = new ObjectMetadata();
//        om.setContentLength(file.length());
//
//        final PutObjectRequest putRequest = new PutObjectRequest(S3_BUCKET_NAME,file.getName(),fis,om);
//
//        // 権限の設定
//        putRequest.setCannedAcl(CannedAccessControlList.PublicRead);
//
//        // upload
//        S3Client.putObject(putRequest);
//        fis.close();
//
//        System.out.println("upload end");
//    }

//    //決め打ちでs3バケットの内容を取得し文字列を返す
//    public String get(final Context context) throws IOException{
//        //クライアント認証
//        AmazonS3 s3Client = authS3Client();
//
//        //s3バケットの内容をInputStreamで取得？
//        S3Object object = s3Client.getObject(new GetObjectRequest(S3_BUCKET_NAME,S3_BUCKET_KEY));
//        InputStream objectData = object.getObjectContent();
//
//        //Stringに変換できるようにする
//        ByteArrayOutputStream result = new ByteArrayOutputStream();
//        byte[] buf = new byte[1024];
//        for (int length; (length = objectData.read(buf)) != -1; ) {
//            result.write(buf, 0, length);
//        }
//        //必要かわからない
//        objectData.close();
//
//        //Stringに変換
//        return result.toString("UTF-8");
//    }

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        System.out.println("upload start");

//        //クライアント認証
//        AmazonS3 S3Client = authS3Client();
//
//        //final PutObjectRequest putRequest = new PutObjectRequest(S3_BUCKET_NAME,file.getName(),fis,om);
//
//        // 権限の設定
//        //putRequest.setCannedAcl(CannedAccessControlList.PublicRead);
//
//        // upload
//        //S3Client.putObject(putRequest);
//
//        S3Object xFile = S3Client.getObject(S3_BUCKET_NAME, S3_BUCKET_KEY);
//        InputStream objectData = xFile.getObjectContent();
//
//        try {
//            ByteArrayOutputStream result = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            for (int length; (length = objectData.read(buffer)) != -1; ) {
//                result.write(buffer, 0, length);
//            }
//            // StandardCharsets.UTF_8.name() > JDK 7
//            return result.toString("UTF-8");
//        } catch (IOException e) {
//            return "IOException";
//        }
        return event.toString();
    }
}
