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

            Console.WriteLine("Check if the app name is available");
            OperationInputs input = new OperationInputs(name);
            var nameAvailable = client.Apps.CheckNameAvailability(input);
            Console.WriteLine(nameAvailable.Message);

            Console.WriteLine("Creating the app");
            client.Apps.CreateOrUpdate(resourceGroup, name, app);

            Console.WriteLine("Getting the app");
            var resultApp = client.Apps.Get(resourceGroup, name);
            Console.WriteLine(resultApp);

            Console.WriteLine("Updating the app");
            var updateApp = new AppPatch();
            updateApp.DisplayName = name + "-new-name";
            var updateResult = client.Apps.Update(resourceGroup, name, updateApp);
            Console.WriteLine(updateResult);

            Console.WriteLine("Listing apps");
            foreach (var currentApp in client.Apps.ListByResourceGroup(resourceGroup))
            {
                Console.WriteLine($"{currentApp.DisplayName} ({currentApp.Id})");
            }

            Console.WriteLine(Environment.NewLine);
            // Console.WriteLine("Removing app");
            // client.Apps.Delete(resourceGroup, name);

            Console.WriteLine("Done");
        }
    }
}
