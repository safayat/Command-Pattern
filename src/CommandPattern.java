import com.sun.deploy.util.StringUtils;

import java.util.*;

/**
 * Created by safayat on 11/22/16.
 */

abstract class Command{
    protected List<String> stringList;
    protected String olddata;

    public Command(List<String> stringList) {
        this.stringList = stringList;
    }

    public abstract void execute();
    public abstract void undo();

    public String getOlddata() {
        return olddata;
    }

    public void setOlddata(String olddata) {
        this.olddata = olddata;
    }
}

class Insert extends Command{
    private String data;
    private int index;
    Insert(List<String> stringList,String str) {
        super(stringList);
        data = str;
    }

    @Override
    public void execute() {
       stringList.add(data);
       index = stringList.size()-1;

    }

    @Override
    public void undo() {
        if(stringList.size()>index){
            stringList.remove(index);
        }
    }

    @Override
    public String toString() {
        return "Insert{" +
                "data='" + data + '\'' +
                '}';
    }
}
class Update extends Command{
    private String data;
    private int index;
    Update(List<String> stringList, int i,String str) {
        super(stringList);
        index = i;
        data = str;
        setOlddata(data);
    }

    @Override
    public void execute() {
        if(stringList.size()>index){
            setOlddata(stringList.get(index));
            stringList.set(index, data);
        }
    }

    @Override
    public void undo() {
        if(stringList.size()> index){
            stringList.set(index,olddata);
        }
    }


    @Override
    public String toString() {
        return "Update{" +
                "data='" + data + '\'' +
                ", index=" + index +
                '}';
    }
}

class Remove extends Command{
    private int index;
    Remove(List<String> stringList,int i) {
        super(stringList);
        index = i;
    }

    @Override
    public void undo() {
            stringList.add(index,olddata);
    }

    @Override
    public void execute() {
        if(stringList.size()>index){
            setOlddata(stringList.get(index));
            stringList.remove(index);
        }
    }

    @Override
    public String toString() {
        return "Remove{" +
                "index=" + index +
                '}';
    }
}




public class CommandPattern {

    public static void executeCommands(Stack<Command> commandStack){
        Iterator<Command> itr = commandStack.iterator();
        while (itr.hasNext()){
            Command command = itr.next();
            command.execute();
        }
    }

    public static void viewCommandStack(Stack<Command> commandStack){
        Iterator<Command> itr = commandStack.iterator();
        while (itr.hasNext()){
            System.out.println(itr.next());
        }
    }
    public static void viewData(List<String> stringList){
        for(String s:stringList){
            System.out.println(s);
        }
    }
    public static void undo(Stack<Command> doStack,Stack<Command> redoStack){
        if(doStack.size()>0){
            Command command = doStack.pop();
            command.undo();
            redoStack.push(command);
        }

    }
    public static void redo(Stack<Command> doStack,Stack<Command> redoStack){
        if(redoStack.size()>0){
            Command command = redoStack.pop();
            command.execute();
            doStack.push(command);

        }

    }
    public static Command parseInstructionAndCreateCommand(String instruction, List<String> stringList){
        try {
            String[] splitted_instruction = instruction.split("[ ]+");
            if(splitted_instruction!=null && splitted_instruction.length>1){
                String op_name = splitted_instruction[0];
                if("PUT".equalsIgnoreCase(op_name)){
                    return new Insert(stringList, splitted_instruction[1]);
                }else if("UP".equalsIgnoreCase(op_name)){
                    int index = Integer.parseInt(splitted_instruction[1]);
                    if(splitted_instruction.length>2){
                        String data = splitted_instruction[2];
                        return new Update(stringList, index,data);
                    }

                }else if("DEL".equalsIgnoreCase(op_name)){
                    int index = Integer.parseInt(splitted_instruction[1]);
                    return new Remove(stringList, index);

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    public static void main(String[] args){
        List<String> strings = new ArrayList<String>();
        Stack<Command> commandStack = new Stack<Command>();
        Stack<Command> redoStack = new Stack<Command>();

        commandStack.push(new Insert(strings,"1 "));
        commandStack.push(new Insert(strings,"2 "));
        commandStack.push(new Insert(strings,"3 "));
        commandStack.push(new Insert(strings,"4 "));
        commandStack.push(new Insert(strings,"5 "));
        commandStack.push(new Insert(strings,"6 "));
        executeCommands(commandStack);
        viewData(strings);

        System.out.println("available commands : cmds, data, undo, redo, put, up, del");
        System.out.println("view command stack: cmds");
        System.out.println("view string list: data");
        System.out.println("To undo: undo");
        System.out.println("To Redo: redo");
        System.out.println("insert command example: put str1");
        System.out.println("update command example:  up 1 str2");
        System.out.println("Delete command example:  del 2");
        Scanner s = new Scanner(System.in);
        while (true){
            String com = s.nextLine();
            if(com.equalsIgnoreCase("CMDS")){
                viewCommandStack(commandStack);
            }else if(com.equalsIgnoreCase("DATA")){
                viewData(strings);
            }else if(com.equalsIgnoreCase("UNDO")){
                undo(commandStack,redoStack);
            }else if(com.equalsIgnoreCase("REDO")){
                redo(commandStack,redoStack);
            }else if(com.equalsIgnoreCase("EXIT")){
                break;
            }else{
                Command command = parseInstructionAndCreateCommand(com,strings);
                if(command !=null){
                    commandStack.push(command);
                    command.execute();
                }

            }
        }





    }

}
