package structuredconcurrency;

import com.intuit.karate.junit5.Karate;

class SCRunner {
    
    @Karate.Test
    Karate testUsers() {
        return Karate.run("structured-concurrency.feature").relativeTo(getClass());
    }    

}
