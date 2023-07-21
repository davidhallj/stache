## Open items in no specific order. 

1. Investigate support for a 'playback' mode that is more stateful
2. Flatten RunStrategy sub-enums - all meaning has been hoisted to the top level 
enum. If a new RunStrategy flow is needed, a new top level enum should be created
3. Add support for other types of web proxy implementations (like Spring Rest)
4. Test @Stache flows / error messaging for if the field value is not properly set
5. Analyze the dependency scope of the log4j impl so-as not to cause a multiple bindings issue in a consuming codebase https://www.slf4j.org/codes.html#multiple_bindings