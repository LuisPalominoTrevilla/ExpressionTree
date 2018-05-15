import java.util.LinkedList;
import java.util.Stack;

/**
 * @author Luis Palomino
 * @category Data Structures
 * @since May 15th 2018
 * 
 */

public class ExpressionTree{
    
    private static final String OPERATORS = "^*/+-";
    
    private TreeNode root;
    
    /**
     * Default constructor
     */
    public ExpressionTree(String expression){
        // Quitamos espacios en blanco
        expression = expression.replaceAll("\\s+","");
        this.root = this.constructTree(expression);
    }
    
    private TreeNode constructTree(String exp){
        Stack<TreeNode> treeStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();
        String[] expression = exp.split("");
        
        for(String e:expression){
            if(!this.isOperator(e)){
                // Si es un número, se mete al treeStack en forma de nodo
                TreeNode temp = new TreeNode(e);
                treeStack.push(temp);
            }else{
                // Si es un operador, se vacía el operatorStack hasta encontrar un operador de menor precedencia, al final se mete en el mismo stack
                this.emptyStack(treeStack, operatorStack, e);
            }
        }
        
        // Vaciar el stack
        this.emptyStack(treeStack, operatorStack, "%"); // % = wildcard para indicar que se vacíe el stack sin introducir ni comparar operadores
        
        return treeStack.pop();
    }
    
    private void emptyStack(Stack<TreeNode> treeStack, Stack<String> operatorStack, String operator){
        
        // Sacar del operatorStack aquellos operadores que tengan mayor o igual precedencia que el actual
        while(!operatorStack.empty() && this.hasEqualOrMorePrecedence(operator, operatorStack.peek())){
            String oldOperator = operatorStack.pop();   // Sacar el operador pasado
            
            TreeNode op = new TreeNode(oldOperator);      // lo convertimos en nodo
            TreeNode rightChild = treeStack.pop();          // Sacar el primer nodo del treeStack (NUNCA va a estar empty
            TreeNode leftChild;
            if(treeStack.empty()){  // En caso de que ya no haya más nodos
                leftChild = null;
            }else{
                leftChild = treeStack.pop();
            }
            
            // Poner hijos al nuevo nodo
            op.rightChild = rightChild;
            op.leftChild = leftChild;
            
            // Meter nuevo nodo de operador al treeStack
            treeStack.push(op);
        }
        
        // Meter el operador actual en el operatorStack a menos de que sea wildcard (%)
        if(!operator.equals("%")){
            operatorStack.push(operator);
        }
    }
    
    private boolean hasEqualOrMorePrecedence(String op1, String op2){
        // Checar si op1 es wildcard y regresar true
        if(op1.equals("%")){
            return true;
        }
        
        // Checa si el operador op2 es tiene mayor o igual precedencia que el op1
        String[] precedencia = {"+-", "*/", "^"};
        int p1 = -1, p2 = -1;     // Valor de precedencia de ambos operandos
        for(int i = 0; i < precedencia.length; i++){
            if(precedencia[i].contains(op1)){
                p1 = i;
            }
            if(precedencia[i].contains(op2)){
                p2 = i;
            }
        }
        return p2 >= p1;
    }
    
    private boolean isOperator(String digit){
        return ExpressionTree.OPERATORS.contains(digit);
    }
    
    
    /**
     * 
     * @return
     */
    public Double evalEquation(){
        String[] posfixEquation = this.postorder().split(" ");
        LinkedList<Double> operandStack = new LinkedList<>();

        for(int i = 0; i < posfixEquation.length; i++){         // Si es un operando
            if(!this.isOperator(posfixEquation[i])){
                operandStack.push(Double.parseDouble(posfixEquation[i]));
                
            }else{    // Si es un operador
                    double oper1 = operandStack.pop();
                    double oper2 = operandStack.pop();
                    
                    
                    switch(posfixEquation[i]){
                    case "*":
                        operandStack.push(oper2*oper1);
                        break;
                    case "/":
                        operandStack.push(oper2/oper1);
                        break;
                    case "+":
                        operandStack.push(oper2+oper1);
                        break;
                    case "-":
                        operandStack.push(oper2-oper1);
                        break;
                    case "^":
                        operandStack.push(Math.pow(oper2, oper1));
                        break;
                    }
            }
        }
        return operandStack.pop();
    }

    
    /**
     * traverse through the tree inorder
     * @return returns a string with the inorder path
     */
    public String inorder(){
        return this.inorder(this.root);
    }
    
    private String inorder(TreeNode node){
        String acum ="";
        if(node == null){
            return "";
        }
        acum += this.inorder(node.leftChild);
        acum += node;
        acum += this.inorder(node.rightChild);
        return acum;
    }

    /**
     * traverse the tree in postorder
     * @return returns a string with the postorder path
     */
    public String postorder(){
        return this.postorder(this.root);
    }
    
    private String postorder(TreeNode node){
        String acum = "";
        if(node == null){
            return "";
        }
        acum += this.postorder(node.leftChild);
        acum += this.postorder(node.rightChild);
        acum += node;
        return acum;
    }
    
    static class TreeNode{
        String value;
        TreeNode rightChild, leftChild;
        
        public TreeNode(String value){
            this.value = value;
        }
        
        public String toString(){
            return value + " ";
        }
    }
    
    public static void main(String[] args){
        ExpressionTree et = new ExpressionTree("5/2*3+2^2-1");
        System.out.println("infix: " + et.inorder());       // Forma infix
        System.out.println("posfix: " + et.postorder());     // Forma posfix
        
        // Imprimir resultado
        System.out.println(et.evalEquation());
    }
}
