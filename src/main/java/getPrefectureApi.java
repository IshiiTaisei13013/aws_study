import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class getPrefectureApi implements RequestHandler<Map<String,String>, String> {

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

    private JSONObject InputStreamToJson (InputStream inputstream){
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject)jsonParser.parse(
                    new InputStreamReader(inputstream, StandardCharsets.UTF_8));
            return jsonObject;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //リクエストで受け取った郵便番号から都道府県を返す
    @Override
    public String handleRequest(Map<String, String> event, Context context) {

        //クライアント認証
        AmazonS3 S3Client = authS3Client();

        //S3からファイルを取得
        S3Object S3File = S3Client.getObject(S3_BUCKET_NAME, S3_BUCKET_KEY);
        InputStream S3ObjectStream = S3File.getObjectContent();

        //Jsonに変換
        JSONObject jsonObject = InputStreamToJson(S3ObjectStream);

        //request本文からKeyを取得
        String requestKey = event.get("postCode");

        if(jsonObject != null) {
            //Keyで取得
            return jsonObject.get(requestKey).toString();
        }else{
            return "jsonObject is null";
        }
    }
}
