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

// TODO: 2021/08/05 ファイルを作成する機能に変更する
// TODO: 2021/08/05 nameとbodyの入れ子構造を受け取れるように

public class CreateHandler extends AbstractS3Crud{
    //受け取ったJson形式のファイルで更新を行う。
    @Override
    public String handleRequest(Map<String,Object> event, Context context) {

        //Jsonが入れ子になっていても型に合わせて取得できる
        Map<String,String> body = (Map<String, String>) event.get("body");

        //クライアント認証
        AmazonS3 S3Client = authS3Client();

        //受け取ったnameとbodyを元にファイルを形成？

        //S3にアップロード
        S3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME,S3_BUCKET_KEY,is,meta));

        //成功メッセージ
        return "S3 file is updated!";
    }
}
