import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DeleteHandler extends AbstractS3Crud{
    //受け取ったKeyでS3上のファイルのKeyを削除する
    //削除後のファイルをアップロードして更新する。
    @Override
    public String handleRequest(Map<String,Object> event, Context context) {

        //Jsonが入れ子になっていても型に合わせて取得できる
        Map<String,String> body = (Map<String, String>) event.get("body");

        //zipcodeのみをリクエスト本文から受け取る
        String zipcode = body.get("zipcode");

        //クライアント認証
        AmazonS3 S3Client = authS3Client();

        //S3からファイルをInputStream型として取得
        S3Object S3File = S3Client.getObject(S3_BUCKET_NAME, S3_BUCKET_KEY);
        InputStream S3ObjectStream = S3File.getObjectContent();

        //取得したS3のファイルをJsonに変換
        JSONObject jsonObject = inputStreamToJSONObject(S3ObjectStream);

        //変換できなかった場合、jsonObjectにはnullが代入される
        if (jsonObject != null) {

            //Keyで削除(zipcodeで削除する)
            jsonObject.remove(zipcode);
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
