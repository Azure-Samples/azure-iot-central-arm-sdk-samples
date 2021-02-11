using System;
using Microsoft.Azure.Management.IotCentral;
using Microsoft.Azure.Management.IotCentral.Models;
using Microsoft.Rest;

namespace IoTCentralArmSDK
{
    class Program
    {
        static void Main()
        {
            // Access token from the azure-cli
            // az account get-access-token
            var token = "";
            var subscriptionId = "";
            var creds = new TokenCredentials(token, "Bearer");

            var client = new IotCentralClient(creds);
            var skuInfo = new AppSkuInfo("ST2");
            var location = "unitedstates";
            var app = new App(location, skuInfo);
            client.SubscriptionId = subscriptionId;

            var name = "csharp-test-app";
            var resourceGroup = "myResourceGroup";

            app.Location = location;
            app.Subdomain = name;
            app.DisplayName = name;

            Console.WriteLine("===Check if the app name is available");
            OperationInputs input = new OperationInputs(name);
            var appNameAvailability = client.Apps.CheckNameAvailability(input);
            
            if (appNameAvailability.NameAvailable == true) {
                Console.WriteLine("app name is available!");
            } else {
                Console.WriteLine($"app name isn't available because {appNameAvailability.Reason}");
            }

            Console.WriteLine("===Creating the app");
            var createApp = client.Apps.CreateOrUpdate(resourceGroup, name, app);
            Console.WriteLine(createApp.Name);
            Console.WriteLine(createApp.DisplayName);

            Console.WriteLine("===Getting the app");
            var resultApp = client.Apps.Get(resourceGroup, name);
            Console.WriteLine(resultApp.Name);
            Console.WriteLine(resultApp.DisplayName);

            Console.WriteLine("===Updating the app");
            var updateApp = new AppPatch();
            updateApp.DisplayName = name + "-new-name";
            var updateResult = client.Apps.Update(resourceGroup, name, updateApp);
            Console.WriteLine(updateResult.Name);
            Console.WriteLine(updateResult.DisplayName);

            Console.WriteLine($"===Listing all the apps under the resource group of {resourceGroup}");
            foreach (var currentApp in client.Apps.ListByResourceGroup(resourceGroup))
            {
                Console.WriteLine($"{currentApp.DisplayName} ({currentApp.Id})");
            }

            Console.WriteLine("===Listing all the operations in iotc");
            foreach (var currentOperation in client.Operations.List())
            {
                Console.WriteLine(currentOperation.Name);
            }

            Console.WriteLine("===Listing all the app templates in iotc");
            foreach (var currentAppTemplate in client.Apps.ListTemplates())
            {
                Console.WriteLine(currentAppTemplate.Name);
            }

            Console.WriteLine("===Removing app");
            client.Apps.Delete(resourceGroup, name);

            Console.WriteLine("Done");
        }
    }
}
