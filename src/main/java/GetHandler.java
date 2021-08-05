import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import org.json.simple.JSONObject;

import java.io.InputStream;
import java.util.Map;

public class GetHandler extends AbstractS3Crud{
    //Query文字列でzipcodeを指定する
    // (ex) http://hogehoge/?zipcode=111-1111

    //受け取ったKeyに応じたValueを返す
    //eventからzipcodeを受け取ってS3上のファイルを参照し、対応した地名、県名を返す
    @Override
    public String handleRequest(Map<String, Object> event, Context context) {

        //eventからKeyを取得(郵便番号を取得)
        String requestKey = (String) event.get("zipcode");

        //クライアント認証
        AmazonS3 S3Client = authS3Client();

        //S3からファイルをInputStream型として取得
        S3Object S3File = S3Client.getObject(S3_BUCKET_NAME, S3_BUCKET_KEY);
        InputStream S3ObjectStream = S3File.getObjectContent();

        //取得したS3のファイルをJsonに変換
        JSONObject jsonObject = inputStreamToJSONObject(S3ObjectStream);

        try {
                //入力された郵便番号を元に地名を取得
                return jsonObject.get(requestKey).toString();
        } catch (NullPointerException e) {
                //入力された郵便番号は見つからなかった
                return "zipcode is not Found.";
        }
    }
}
