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

	// There are multiple way to authericate with Azure, we picked the device token way which is
	// Sign in through https://microsoft.com/devicelogin (works for account with 2FA)
	// Service Principals in Azure AD would get you information for the following
	// make sure to have your app set for the Redirect URIs, otherwise, it won't work
	// Check out this article in case you want other ways to authericate https://docs.microsoft.com/en-us/azure/go/azure-sdk-go-authorization
	// sample code for Authentication with Azure, check this out https://github.com/Azure/azure-sdk-for-go#authentication
	applicationID := "" // client id
	tenantID := ""
	deviceConfig := auth.NewDeviceFlowConfig(applicationID, tenantID)
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
