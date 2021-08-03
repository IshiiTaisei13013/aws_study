import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PutPrefecture implements RequestHandler<Map<String, Object>, String> {

    //Lambdaの環境変数を読み込む
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
    private JSONObject inputStreamToJson(InputStream is) {

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

    //受け取ったKey,ValueでS3ファイルを更新する
    @Override
    public String handleRequest(Map<String,Object> event, Context context) {

        //Jsonが入れ子になっていても型に合わせて取得できる
        Map<String,String> body = (Map<String, String>) event.get("body");

        String address = body.get("address");
        String zipcode = body.get("zipcode");

        //クライアント認証
        AmazonS3 S3Client = authS3Client();

        //S3からファイルをInputStream型として取得
        S3Object S3File = S3Client.getObject(S3_BUCKET_NAME, S3_BUCKET_KEY);
        InputStream S3ObjectStream = S3File.getObjectContent();

        //取得したS3のファイルをJsonに変換
        JSONObject jsonObject = inputStreamToJson(S3ObjectStream);

        //変換できなかった場合、jsonObjectにはnullが代入される
        if (jsonObject != null) {

            //新しいkeyとvalueを追記
            jsonObject.put(zipcode,address);
        } else {
            //S3の読み込みが失敗または空の可能性がある。
            return "jsonObject is null";
        }

        //JSONObjectをInputStreamに変換
        String str = jsonObject.toString();
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));

        //metadataを作成
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(str.length());

        //S3にアップロード
        S3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME,S3_BUCKET_KEY,is,meta));

        //成功メッセージ
        return "S3 file is updated!";
    }
}
