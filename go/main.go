package main

import (
	"context"
	"fmt"
	"os"

	"github.com/Azure/azure-sdk-for-go/services/iotcentral/mgmt/2021-06-01/iotcentral"
	"github.com/Azure/go-autorest/autorest/azure/auth"
)

func main() {
	subscriptionID := "add-subscription-id-here"
	ioTCentralClient := iotcentral.NewAppsClient(subscriptionID)
	operationsClient := iotcentral.NewOperationsClient(subscriptionID)

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
	applicationID := "add-app-id-here" // client id
	directoryID := "add-directory-id-here"   // tenant id
	deviceConfig := auth.NewDeviceFlowConfig(applicationID, directoryID)
	authorizer, authorizerErr := deviceConfig.Authorizer()
	if authorizerErr != nil {
		fmt.Println("Error authorizing.")
		fmt.Println(authorizerErr)
		os.Exit(1)
	} else {
		ioTCentralClient.Authorizer = authorizer
		operationsClient.Authorizer = authorizer
	}

	resourceDisplayName := "resource-display-name"
	resourceDomainName := "resource-unique-url-id"
	resourceGroup := "myResourceGroup"
	location := "eastus2"
	operationInputs := iotcentral.OperationInputs{
		Name: &resourceDomainName,
	}

	// check if the resource domain name is available
	nameAvailResult, nameAvailErr := ioTCentralClient.CheckNameAvailability(context.Background(), operationInputs)
	if nameAvailErr != nil {
		fmt.Println(nameAvailErr)
		os.Exit(1)
	} else if *nameAvailResult.NameAvailable == true {
		fmt.Println("Resource domain name is available. Let's continue!")
	} else {
		fmt.Println("Resource domain name is not available")
		fmt.Printf("Reason: %v\n", *nameAvailResult.Reason)
		fmt.Printf("Message: %v\n", *nameAvailResult.Message)
		os.Exit(1)
	}

	appInvalidSkuInfo := iotcentral.AppSkuInfo{
		Name: "S1",
	}
	appSku := iotcentral.AppSkuST2
	appSkuInfo := iotcentral.AppSkuInfo{
		Name: appSku,
	}
	appProperties := iotcentral.AppProperties{
		DisplayName: &resourceDisplayName,
		Subdomain:   &resourceDomainName,
	}
	appIdentity := iotcentral.SystemAssignedServiceIdentity{
		Type: iotcentral.SystemAssignedServiceIdentityTypeSystemAssigned,
	}
	app := iotcentral.App{
		AppProperties: &appProperties,
		Sku:           &appSkuInfo,
		Name:          &resourceDomainName,
		Location:      &location,
		Identity:      &appIdentity,
	}
	appInvalidSku := iotcentral.App{
		AppProperties: &appProperties,
		Sku:           &appInvalidSkuInfo,
		Name:          &resourceDomainName,
		Location:      &location,
		Identity:      &appIdentity,
	}

	// fail to create invalid sku app
	failCreateResult, failCreateErr := ioTCentralClient.CreateOrUpdate(context.Background(), resourceGroup, resourceDomainName, appInvalidSku)
	if failCreateErr != nil {
		fmt.Println(failCreateResult.Status() + " to create/update invalid app")
		fmt.Println(failCreateErr)
	} else {
		fmt.Println(failCreateResult.Status() + " to create/update invalid app")
		os.Exit(1)
	}

	// create app
	createResult, createErr := ioTCentralClient.CreateOrUpdate(context.Background(), resourceGroup, resourceDomainName, app)
	if createErr != nil {
		fmt.Println(createResult.Status() + " to create/update app")
		fmt.Println(createErr)
		os.Exit(1)
	} else {
		fmt.Println(createResult.Status() + " to create/update app")
	}

	// get app
	getResult, getErr := ioTCentralClient.Get(context.Background(), resourceGroup, resourceDomainName)
	if getErr != nil {
		fmt.Println(getErr)
		os.Exit(1)
	} else {
		fmt.Printf("App id is %v\n", *getResult.ApplicationID)
	}

	updatedResourceDisplayName := resourceDisplayName + "-new-name"
	updateAppProperties := iotcentral.AppProperties{
		DisplayName: &updatedResourceDisplayName,
		Subdomain:   &resourceDomainName,
	}
	appPatch := iotcentral.AppPatch{
		AppProperties: &updateAppProperties,
	}

	// update app
	updateResult, updateErr := ioTCentralClient.Update(context.Background(), resourceGroup, resourceDomainName, appPatch)
	if updateErr != nil {
		fmt.Println(updateErr)
		os.Exit(1)
	} else {
		fmt.Println(updateResult.Status() + " to update app")
	}

	// list all apps under the resource group
	listAppResult, listAppErr := ioTCentralClient.ListByResourceGroup(context.Background(), resourceGroup)
	if listAppErr != nil {
		fmt.Println(listAppErr)
		os.Exit(1)
	} else {
		fmt.Printf("Here are all the iotc app that reside in the %v resource group,\n", resourceGroup)
		apps := listAppResult.Values()
		for i := range apps {
			fmt.Printf("%v. %v\n", i, *apps[i].AppProperties.DisplayName)
		}
	}

	// list all the operations that is supported by iotc
	operationsResult, operationsErr := operationsClient.List(context.Background())
	if operationsErr != nil {
		fmt.Println(operationsErr)
		os.Exit(1)
	} else {
		fmt.Print("Here are all the supported operations in the iotc,\n")
		operations := operationsResult.Values()
		for i := range operations {
			fmt.Printf("%v. %v\n", i, *operations[i].Display.Operation)
		}
	}

	// list all the iotc app templates
	appTemplatesResult, appTemplatesErr := ioTCentralClient.ListTemplates(context.Background())
	if appTemplatesErr != nil {
		fmt.Println(appTemplatesErr)
		os.Exit(1)
	} else {
		fmt.Print("Here are all the iotc app templates,\n")
		appTemplates := appTemplatesResult.Values()
		for i := range appTemplates {
			fmt.Printf("%v. %v\n", i, *appTemplates[i].Name)
		}
	}

	// delete app
	deleteAppResult, deleteAppErr := ioTCentralClient.Delete(context.Background(), resourceGroup, resourceDomainName)
	if deleteAppErr != nil {
		fmt.Println(deleteAppErr)
		os.Exit(1)
	} else {
		fmt.Println(deleteAppResult.Status() + " to delete app")
	}
}
