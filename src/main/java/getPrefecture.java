import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class getPrefecture implements RequestHandler<Map<String, String>, String> {

    //環境変数の読み込み
    static final String S3_BUCKET_NAME = System.getenv("S3_BUCKET_NAME");
    static final String S3_BUCKET_KEY = System.getenv("S3_BUCKET_KEY");

    //クライアント認証用の関数
    //実行ロールをlambdaにアタッチしないと使えない
    private AmazonS3 authS3Client() {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .build();
        return s3Client;
    }

    //InputStream型をJSONObject型に変換する関数
    private JSONObject InputStreamToJson(InputStream inputstream) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
            jsonObject = (JSONObject) jsonParser.parse(inputStreamReader);
            return jsonObject;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        //失敗したときにNullを返す
        return null;
    }

    //受け取ったKeyに応じたValueを返す
    //eventからzipcodeを受け取ってS3上のファイルを参照し、対応した地名、県名を返す
    @Override
    public String handleRequest(Map<String, String> event, Context context) {

        //eventからKeyを取得(郵便番号を取得)
        String requestKey = event.get("zipcode");

        //クライアント認証
        AmazonS3 S3Client = authS3Client();

        //S3からファイルをInputStream型として取得
        S3Object S3File = S3Client.getObject(S3_BUCKET_NAME, S3_BUCKET_KEY);
        InputStream S3ObjectStream = S3File.getObjectContent();

        //取得したS3のファイルをJsonに変換
        JSONObject jsonObject = InputStreamToJson(S3ObjectStream);

        //変換できなかった場合、jsonObjectにはnullが代入される
        if (jsonObject != null) {
            try {
                //入力された郵便番号を元に地名を取得
                return jsonObject.get(requestKey).toString();
            } catch (NullPointerException e) {
                //入力された郵便番号は見つからなかった
                return "zipcode is not Found.";
            }
        } else {
            //S3の読み込みが失敗または空の可能性がある。
            return "jsonObject is null";
        }
    }
}
