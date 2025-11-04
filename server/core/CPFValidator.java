package server.core;

public class CPFValidator {

    public static boolean validate(String cpf) {
        
        if (cpf == null || cpf.trim().isEmpty())
            return false;

        String cleanedCpf = cpf.replace(".", "").replace("-","");

        if (cleanedCpf.length() != 11)
            return false;
        
        for (char c : cleanedCpf.toCharArray()) {
            if(!Character.isDigit(c)) 
                return false;
        }

        /* for (char c : cleanedCpf.toCharArray()) {
            if (c < '0' || c > '9') 
                return false;
        } 
                
        for (int i = 0; i < tamanho; i++) {
            char c = cleanedCpf.charAt(i);
            if (c < '0' || c > '9')
                return false;
        } */ 

        return true;
    }
}