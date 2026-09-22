$services = @(
    @{
        name="discovery-server";
        deps="cloud-eureka-server,actuator"
    },
    @{
        name="api-gateway";
        deps="cloud-gateway,cloud-eureka,actuator,cloud-loadbalancer"
    },
    @{
        name="auth-service";
        deps="web,security,data-jpa,h2,cloud-eureka,actuator"
    },
    @{
        name="package-service";
        deps="web,data-jpa,h2,cloud-eureka,actuator"
    },
    @{
        name="payment-service";
        deps="web,data-jpa,h2,cloud-eureka,actuator"
    },
    @{
        name="booking-service";
        deps="web,data-jpa,h2,cloud-eureka,cloud-feign,actuator"
    }
)

foreach ($svc in $services) {
    $name = $svc.name
    $deps = $svc.deps
    Write-Host "Downloading $name..."
    $url = "https://start.spring.io/starter.zip?type=maven-project&language=java&baseDir=$name&groupId=com.voyagecraft&artifactId=$name&name=$name&description=$name&packageName=com.voyagecraft.$($name.Replace('-',''))&packaging=jar&javaVersion=17&dependencies=$deps"
    Invoke-WebRequest -Uri $url -OutFile "$name.zip"
    if (Test-Path "$name.zip") {
        Expand-Archive -Path "$name.zip" -DestinationPath "." -Force
        Remove-Item "$name.zip"
    }
}
Write-Host "Done!"
