package main

import (
	"context"
	"fmt"

	"github.com/Azure/azure-sdk-for-go/services/iotcentral/mgmt/2018-09-01/iotcentral"
	"github.com/Azure/go-autorest/autorest/azure/auth"
)

func main() {
	subscriptionID := ""
	ioTCentralClient := iotcentral.NewAppsClient(subscriptionID)

	// Before you begin, please make sure to register an app with Azure Active Directory first.
	// Follow this article, https://docs.microsoft.com/powerapps/developer/common-data-service/walkthrough-register-app-azure-active-directory
	// Remember to add Azure Service Management - user_impersonation for API Permission in your registered app.
	// There are multiple ways to authericate with Azure, we picked the device token way which is
	// sign in through https://microsoft.com/devicelogin (also works for account with 2FA)
	// Once you have registered your app, you should be able to get the applicationId and directoryID from there.
	// Please keep in mind that you need to set "allowPublicClient" as true in your registered app's manifest.
	// You also need to set the Redirect URIs, otherwise, it won't work.
	// Check out this article in case you are interested in other ways to authericate https://docs.microsoft.com/azure/go/azure-sdk-go-authorization
	// sample code for Authentication with Azure, check out this readme, https://github.com/Azure/azure-sdk-for-go#authentication
	applicationID := "" // client id
	directoryID := ""   // tenant id
	deviceConfig := auth.NewDeviceFlowConfig(applicationID, directoryID)
	authorizer, authorizerErr := deviceConfig.Authorizer()
	if authorizerErr == nil {
		ioTCentralClient.Authorizer = authorizer
	}

	resourceName := "some-app-name"
	resourceGroup := "myResourceGroup"
	location := "unitedstates"
	operationInputs := iotcentral.OperationInputs{
		Name: &resourceName,
	}

	// check name available
	nameAvailResult, nameAvailErr := ioTCentralClient.CheckNameAvailability(context.Background(), operationInputs)
	if nameAvailErr != nil {
		fmt.Println(nameAvailResult.NameAvailable)
	}

	appSku := iotcentral.ST2
	appSkuInfo := iotcentral.AppSkuInfo{
		Name: appSku,
	}
	appProperties := iotcentral.AppProperties{
		DisplayName: &resourceName,
		Subdomain:   &resourceName,
	}
	app := iotcentral.App{
		AppProperties: &appProperties,
		Sku:           &appSkuInfo,
		Name:          &resourceName,
		Location:      &location,
	}

	// create app
	createResult, createErr := ioTCentralClient.CreateOrUpdate(context.Background(), resourceGroup, resourceName, app)
	if createErr == nil {
		fmt.Println(createResult.Response())
	}

	// get app
	getResult, getErr := ioTCentralClient.Get(context.Background(), resourceGroup, resourceName)
	if getErr == nil {
		fmt.Println(getResult.ApplicationID)
	}

	updateResourceName := resourceName + "-new-name"
	updateAppProperties := iotcentral.AppProperties{
		DisplayName: &updateResourceName,
		Subdomain:   &resourceName,
	}
	appPatch := iotcentral.AppPatch{
		AppProperties: &updateAppProperties,
	}

	// update app
	updateResult, updateErr := ioTCentralClient.Update(context.Background(), resourceGroup, resourceName, appPatch)
	if updateErr == nil {
		fmt.Println(updateResult.Response())
	}

	// list all apps under the resource group
	listAppResult, listAppErr := ioTCentralClient.ListByResourceGroup(context.Background(), resourceGroup)
	if listAppErr == nil {
		apps := listAppResult.Values()
		for i := range apps {
			fmt.Println(*apps[i].AppProperties.DisplayName)
		}
	}

	// delete app
	// deleteAppResult, deleteAppErr := ioTCentralClient.Delete(context.Background(), resourceGroup, resourceName)
	// if deleteAppErr == nil {
	// 	fmt.Println(deleteAppResult.Status())
	// }
}
