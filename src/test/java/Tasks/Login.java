package Tasks;

import Functions.SeleniumFunctions;

public class Login {
    SeleniumFunctions functions = new SeleniumFunctions();


    public void login(String ambiente, String actor) throws Exception {


        functions.readMain(ambiente);
        functions.iLoadTheDOMInformation("Principal.json");

    }


}
