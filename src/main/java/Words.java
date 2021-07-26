
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

import java.io.InputStream;

public class Words {
    public String get(final Context context){
        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        S3Object object = s3Client.getObject(new GetObjectRequest("regaeve-bucket-01","greeting"));
        InputStream objectData = object.getObjectContent();
        return "How Are you?";
    }
}
