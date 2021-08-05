import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.json.simple.JSONObject;

import java.io.InputStream;
import java.util.Map;

public class PutHandler
        extends AbstractS3Crud
        implements RequestHandler<Map<String, Object>, String>{

    // POST methodで受け取ったリクエスト本文の
    // zipcodeとaddressでS3上のファイルを更新する
    // 想定するリクエスト本文の形式 (eventの中身)
    // {
    //      "body":{
    //              "zipcode" : "111-1111",
    //              "address" : "hogehoge"
    //              }
    // }
    //

    @Override
    public String handleRequest(Map<String,Object> event, Context context) {

        //Jsonが入れ子になっていても型に合わせて取得できる
        Map<String,String> body = (Map<String, String>) event.get("body");

        //リクエスト本文からkeyとvalueを取得
        String zipcode = body.get("zipcode");
        String address = body.get("address");

        //クライアント認証
        AmazonS3 S3Client = authS3Client();

        //S3からファイルをInputStream型として取得
        S3Object S3File = S3Client.getObject(S3_BUCKET_NAME, S3_BUCKET_KEY);
        InputStream S3ObjectStream = S3File.getObjectContent();

        //取得したS3のファイルをJsonに変換
        JSONObject jsonObject = inputStreamToJSONObject(S3ObjectStream);

        //keyとvalueを追記
        jsonObject.put(zipcode,address);

        //InputStream型に戻す
        InputStream is = jSONObjectToInputStream(jsonObject);

        //metadataを作成
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(jsonObject.toString().length());

        //S3にアップロード
        S3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME,S3_BUCKET_KEY,is,meta));

        //成功メッセージ
        return "S3 file is updated!!";
    }
}
