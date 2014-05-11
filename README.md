# Maven Modules

    machine ( pom )
        machine-humanity-api    ( api defintion )
        machine-backbone-api    ( api defintion )
        machine-management-api  ( api defintion )
        machine-message-api     ( api defintion )

        machine-lib-service     ( library )
        machine-lib-message     ( library )

        machine-humanity        ( executable )
        machine-backbone        ( executable )
        machine-management      ( executable )

        bot-demo ( pom )
            bot-demo-messages   ( api defintion )
            bot-demo-consumer   ( executable )
            bot-demo-master     ( executable )

# Ports

No modules conflict with other moduless default configurations.

    machine-management = 80
    machine-humanity   = 81
    machine-backbone   = 82

    bot-demo-master    = 8080
    bot-demo-consumer  = 8081

All consumer projects can query the local machine-backbone instance for the machine-management URL.