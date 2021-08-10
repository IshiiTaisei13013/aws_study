import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.util.Map;

public class CreateHandler extends AbstractS3Crud{
    //受け取った名前のファイルを作成する
    // 想定するリクエスト本文の形式 (eventの中身)
    // {
    //      "body":{
    //              "name" : "newFile.json",
    //              }
    // }
    //

    @Override
    public String handleRequest(Map<String,Object> event, Context context) {

        //Jsonが入れ子になっていても型に合わせて取得できる
        Map<String,String> body = (Map<String, String>) event.get("body");

        //クライアント認証
        AmazonS3 S3Client = authS3Client();

        String name = body.get("name");

        //S3にアップロード
        S3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME,name,null,null));

        //成功メッセージ
        return "S3 file is created!";
    }
}