import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.*;

import com.amazonaws.services.s3.model.S3Object;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class getPrefecture implements RequestHandler<Map<String, String>, String> {

    //環境変数の読み込み
    static final String S3_BUCKET_NAME = System.getenv("S3_BUCKET_NAME");
    static final String S3_BUCKET_KEY = System.getenv("S3_BUCKET_KEY");

    //環境変数から指定するとき用
    //static final String S3_JSON_KEY = System.getenv("S3_JSON_KEY");

    private AmazonS3 authS3Client() {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .build();
        return s3Client;
    }

    //InputStream型をJSONObject型に変換
    //失敗するとNullを返す
    private JSONObject InputStreamToJson(InputStream inputstream) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(
                    new InputStreamReader(inputstream, StandardCharsets.UTF_8));
            return jsonObject;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //イベント確認用のハンドラ
//    @Override
//    public String handleRequest(Map<String, String> event, Context context) {
//
//        return event.get("postCode");
//    }

    //リクエスト文で受け取ったKeyに応じたValueを返す
    //postCodeを受け取って対応した地名、県名を返す
    @Override
    public String handleRequest(Map<String, String> event, Context context) {

        //クライアント認証
        AmazonS3 S3Client = authS3Client();

        //S3からファイルを取得
        S3Object S3File = S3Client.getObject(S3_BUCKET_NAME, S3_BUCKET_KEY);
        InputStream S3ObjectStream = S3File.getObjectContent();

        //取得したS3のファイルをJsonに変換
        JSONObject jsonObject = InputStreamToJson(S3ObjectStream);

        //request本文からKeyを取得
        String requestKey = event.get("postCode");

        //変換できなかった場合、jsonObjectにはnullが代入される
        if (jsonObject != null) {
            try {
                //Keyで取得
                return jsonObject.get(requestKey).toString();
            }catch (NullPointerException e){
                return "postCode is not Found.";
            }
        } else {
            return "jsonObject is null";
        }
    }
}
