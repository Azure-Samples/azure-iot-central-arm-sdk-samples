import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.resourcemanager.iotcentral.IotCentralManager;

public class IotCentralMgmtApiSampleBase {
    protected static final String AZURE_CLIENT_ID = "Azure Client ID";
    protected static final String AZURE_CLIENT_SECRET = "Azure Client Secret";
    protected static final String AZURE_TENANT_ID = "Azure Tenant ID";
    protected static final String AZURE_SUBSCRIPTION_ID = "Azure Subscription";

    protected static IotCentralManager iotCentralManager;

    static {
        authenticateToAzure();
    }

    protected static IotCentralManager CreateIoTCentralManager() {
        AzureProfile profile = new AzureProfile(AZURE_TENANT_ID, AZURE_SUBSCRIPTION_ID, AzureEnvironment.AZURE);
        TokenCredential credential = createToken();
        IotCentralManager manager = IotCentralManager.authenticate(credential, profile);

        return manager;
    }

    protected static TokenCredential createToken() {
        return new ClientSecretCredentialBuilder()
                .clientSecret(AZURE_CLIENT_SECRET)
                .tenantId(AZURE_TENANT_ID)
                .clientId(AZURE_CLIENT_ID)
                .build();
    }

    private static void authenticateToAzure() {
        // Authentication for general IOTC service
        AzureProfile profile = new AzureProfile(AZURE_TENANT_ID, AZURE_SUBSCRIPTION_ID, AzureEnvironment.AZURE);
        TokenCredential credential = createToken();
        iotCentralManager = IotCentralManager.authenticate(credential, profile);
    }

}
