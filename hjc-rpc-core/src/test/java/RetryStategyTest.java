import com.hjc.hjcrpc.fault.retry.FixedIntervalRetryStrategy;
import com.hjc.hjcrpc.fault.retry.NoRetryStrategy;
import com.hjc.hjcrpc.fault.retry.RetryStrategy;
import com.hjc.hjcrpc.model.RpcResponse;
import org.junit.Test;

/**
 * File Description: RetryStategyTest
 * Author: hou-jch
 * Date: 2024/7/11
 */

public class RetryStategyTest {
    RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void doRestry() throws Exception {
//        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("重试测试");
                return aaa();

            });
            System.out.println(rpcResponse);
//        }catch (Exception e){

//            System.out.println("重试多次失败");
//            e.printStackTrace();
//        }

    }

    public RpcResponse aaa(){
        throw new RuntimeException("重试失败");
    }
}
