import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public abstract class AbstractS3Crud {

    //Lambdaの環境変数を読み込む
    static final String S3_BUCKET_NAME = System.getenv("S3_BUCKET_NAME");
    static final String S3_BUCKET_KEY = System.getenv("S3_BUCKET_KEY");

    //クライアント認証用の関数
    //実行ロールをlambdaにアタッチしないと使えない
    public AmazonS3 authS3Client() {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .build();

        return s3Client;
    }

    //InputStream型をJSONObject型に変換する関数
    public JSONObject inputStreamToJSONObject(InputStream is) {

        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);

        try (isr) {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(isr);
            return jsonObject;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        //どこかで失敗したらnullを返す
        return null;
    }

    //JSONObject型をInputStream型に変換
    public InputStream jSONObjectToInputStream(JSONObject jo){
        String str = jo.toString();
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));

        return is;
    }

}
