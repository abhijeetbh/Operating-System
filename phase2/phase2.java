import java.io.*;
import java.util.*;

public class phase2{

        public static char[][] memory=new char[300][4];
        public static char[][] virtualMemory=new char[100][4];
        public static int IC;
        public static char[] R=new char[4];
        public static char[] IR=new char[4];
        public static char[] PTR=new char[4];
        public static int SI;
        public static int TI;
        public static int PI;
        public static int C;
        public static char[] buffer=new char[40];
        public static int numberOfInstructions;
        public static int TLC;
        public static int TTL;
        public static int LLC;
        public static int TLL;
        public static int calledByRead;
        public static int calledBySR;
        public static int frameAssignedDuringValid;
        public static ArrayList<Integer> frameNumbers=new ArrayList<Integer>();
        public static int frameIndex;
        public static int pageNumber;
        public static int errorDetected;
        public static int pageTableLoc;
        public static int pageTableAT;
        public static int errorLineLimit;
        public static int errorOutOfData;

        static PCB pro=new PCB();
        public static void main(String[] args) throws Exception{
                int i;
                load();
        }

        public static void displayMemoryContents(){
                int i;
                System.out.println();
                for(i=0; i<300; i++){
                        if(i%10==0 && i!=0)
                                System.out.println("-----------------------------------------------------------------");
                        System.out.println(i+"  "+memory[i][0]+memory[i][1]+memory[i][2]+memory[i][3]);
                }
                System.out.println();
                System.out.println();
                System.out.println("===========PCB==========");
                System.out.println("Process ID : "+pro.processID);
                System.out.println("Total Time Limit : "+pro.totalTimeLimit);
                System.out.println("Total Line Limit : "+pro.totalLineLimit);
                System.out.println("Time Given : "+pro.completionTime);
                System.out.println("Lines Printed : "+pro.linesPrinted);
                System.out.println("Current Status : "+pro.currentStatus);
                System.out.println("\n\n\n\n\n\n");
        }

        public static void load() throws Exception{
                String line;
                int i;
                pageNumber=49;

                String subline;
                int linePointer=0;
                int insCounter=0;
                int frameToFill;
                File f=new File("withErrors.txt");
                Scanner sc=new Scanner(f);
                while(sc.hasNextLine()){
                        line=sc.nextLine();
                        line=line.trim();
                        System.out.println(line);
                        if(line.length()>4)
                                subline=line.substring(0,4);
                        else
                                subline=line;
                        if(subline.equals("$AMJ")){
                                linePointer=0;
                                insCounter=0;
                                pro.processID=Integer.parseInt(line.substring(4,8));
                                TTL=Integer.parseInt(line.substring(8,12));
                                pro.totalTimeLimit=TTL;
                                TLL=Integer.parseInt(line.substring(12));
                                pro.totalLineLimit=TLL;
                                initialize();
                        }
                        else if(subline.equals("$DTA")){
                                pro.currentStatus="EXECUTE";
                                executeProgram(sc);
                        }
                        else if(subline.equals("$END")){
                                pro.currentStatus="FINISHED";
                                displayMemoryContents();
                                continue;
                        }
                        else{
                                pro.currentStatus="FETCH";
                                insCounter=0;
                                line=line.trim();
                                char[] instructions=line.toCharArray();
                                for(i=0; i<instructions.length; i++)
                                        buffer[i]=instructions[i];

                                frameToFill=allocate();
                                String idk=Integer.toString(frameToFill);
                                if(frameToFill<10)
                                        idk="0"+idk;
                                char[] frameAsChar=idk.toCharArray();
                                memory[pageTableLoc][0]='P';
                                memory[pageTableLoc][1]=(char)pageNumber;
                                pageNumber++;
                                memory[pageTableLoc][2]=frameAsChar[0];
                                memory[pageTableLoc][3]=frameAsChar[1];
                                pageTableLoc++;
                                linePointer=frameToFill*10;
                                for(i=0; i<instructions.length; i++){
                                        if(insCounter!=0 && insCounter%4==0){
                                                linePointer++;
                                                numberOfInstructions++;
                                        }
                                        if(instructions[i]=='H'){
                                                memory[linePointer][insCounter%4]=buffer[i];
                                                numberOfInstructions++;
                                                insCounter=((insCounter/4)+1)*4;
                                        }
                                        else{
                                                memory[linePointer][insCounter%4]=buffer[i];
                                                insCounter++;
                                                if(i==(instructions.length-1))
                                                        numberOfInstructions++;
                                        }
                                }
                                for(i=0; i<40; i++)
                                        buffer[i]='_';
                        }
                }
        }

        public static int allocate(){
                int n=frameNumbers.get(frameIndex);
                frameIndex++;
                return n;
        }

        public static void initialize() throws Exception{
                int i;
                IC=0;
                SI=0;
                TI=0;
                PI=0;
                TLC=0;
                LLC=0;
                pageNumber=49;
                errorDetected=0;
                numberOfInstructions=0;
                errorLineLimit=0;
                errorOutOfData=0;
                C=0;
                calledByRead=0;
                calledBySR=0;
                frameAssignedDuringValid=0;
                R[0]='_';
                R[1]='_';
                R[2]='_';
                R[3]='_';
                IR[0]='_';
                IR[1]='_';
                IR[2]='_';
                IR[3]='_';
                frameIndex=0;


                frameNumbers.clear();

                for(i=0; i<30; i++)
                        frameNumbers.add(i);
                Collections.shuffle(frameNumbers);
                int temp=allocate();
                pageTableLoc=temp*10;
                String frame=Integer.toString(temp);
                if(temp<10)
                        frame="00"+frame+"0";
                else
                        frame="0"+frame+"0";

                char[] r=frame.toCharArray();

                PTR[0]=r[0];
                PTR[1]=r[1];
                PTR[2]=r[2];
                PTR[3]=r[3];
                System.out.println("PTR : "+PTR[0]+PTR[1]+PTR[2]+PTR[3]);

                for(i=0; i<300; i++){
                        memory[i][0]='_';
                        memory[i][1]='_';
                        memory[i][2]='_';
                        memory[i][3]='_';
                }
                for(i=0; i<100; i++){
                        virtualMemory[i][0]='_';
                        virtualMemory[i][1]='_';
                        virtualMemory[i][2]='_';
                        virtualMemory[i][3]='_';
                }
                for(i=0; i<40; i++)
                        buffer[i]='_';
                File of=new File("output.txt");
                if(of.createNewFile())
                        System.out.println("Created an output file : output.txt");
        }

        public static int addressTranslate(String va, Scanner sc) throws Exception{
                int virtualAddress=0;
                try{
                        virtualAddress=Integer.parseInt(va);
                }
                catch(Exception e){
                        PI=2;
                        masterMode(sc);
                }

                String pageTable=""+PTR[0]+PTR[1]+PTR[2]+PTR[3];
                int ptrValue=Integer.parseInt(pageTable);
                int pte=ptrValue+(virtualAddress/10);
                pageTableAT=pte;
                if(memory[pte][0]!='P'){
                        PI=3;
                        masterMode(sc);
                        if(PI!=3){
                                int realAddress=frameAssignedDuringValid*10;
                                realAddress=realAddress+virtualAddress%10;
                                return realAddress;
                        }
                        else
                                return -1;
                }
                else{
                        String temp=""+memory[pte][2]+memory[pte][3];
                        int frameNo=Integer.parseInt(temp);
                        int realAddress=(frameNo*10)+(virtualAddress%10);
                        return realAddress;
                }
        }

        public static void executeProgram(Scanner sc) throws Exception{
                IC=0;
                int i,address;
                int realAddress;
                String temporary;
                while(IC!=numberOfInstructions){
                        pro.completionTime=TLC;
                        if(TLC>=TTL)
                                TI=2;
                        System.out.println("\n\nValue of IC : "+IC);
                        temporary="";
                        address=0;
                        String temporary2=""+IC;
                        realAddress=addressTranslate(temporary2,sc);
                        System.out.println("Real Address corresponding to IC "+IC+" is : "+realAddress);

                        for(i=0; i<4; i++)
                                IR[i]=memory[realAddress][i];
                        if(IR[0]=='G' && IR[1]=='D'){
                                System.out.println(""+IR[0]+IR[1]+IR[2]+IR[3]);
                                IC++;

                                IR[3]='0';
                                SI=1;
                                masterMode(sc);
                                TLC++;
                        }
                        else if(IR[0]=='P' && IR[1]=='D'){
                                System.out.println(""+IR[0]+IR[1]+IR[2]+IR[3]);
                                IC++;
                                IR[3]='0';
                                SI=2;
                                masterMode(sc);
                                TLC++;
                        }
                        else if(IR[0]=='H' && IR[1]=='_'){
                                System.out.println(""+IR[0]+IR[1]+IR[2]+IR[3]);
                                IC++;
                                SI=3;
                                TLC++;
                                masterMode(sc);
                                break;
                        }
                        else if(IR[0]=='L' && IR[1]=='R'){
                                System.out.println(""+IR[0]+IR[1]+IR[2]+IR[3]);
                                IC++;
                                temporary=""+IR[2]+IR[3];
                                address=addressTranslate(temporary,sc);
                                for(i=0; i<4; i++)
                                        R[i]=memory[address][i];
                                TLC++;
                        }
                        else if(IR[0]=='S' && IR[1]=='R'){
                                System.out.println(""+IR[0]+IR[1]+IR[2]+IR[3]);
                                IC++;
                                temporary=""+IR[2]+IR[3];
                                calledBySR=1;
                                address=addressTranslate(temporary,sc);
                                for(i=0; i<4; i++)
                                        memory[address][i]=R[i];
                                TLC++;
                        }
                        else if(IR[0]=='C' && IR[1]=='R'){
                                System.out.println(""+IR[0]+IR[1]+IR[2]+IR[3]);
                                IC++;
                                temporary=""+IR[2]+IR[3];
                                address=addressTranslate(temporary,sc);
                                for(i=0; i<4; i++){
                                        if(R[i]==memory[address][i]){
                                                C=1;
                                        }
                                        else{
                                                C=0;
                                                break;
                                        }
                                }
                                TLC++;
                        }
                        else if(IR[0]=='B' && IR[1]=='T'){
                                System.out.println(""+IR[0]+IR[1]+IR[2]+IR[3]);
                                if(C==1){
                                        temporary=""+IR[2]+IR[3];
                                        address=Integer.parseInt(temporary);
                                        IC=address;
                                }
                                else{
                                        IC++;
                                }
                                TLC++;
                        }
                        else{
                                System.out.println(""+IR[0]+IR[1]+IR[2]+IR[3]);
                                System.out.println("No opcode matches");

                                PI=1;
                                masterMode(sc);
                        }
                }
        }


        public static void masterMode(Scanner sc) throws Exception{
                System.out.println("Entered masterMode()");
                System.out.println("SI : "+SI);
                System.out.println("TI : "+TI);
                System.out.println("PI : "+PI);
                if(TLC>=TTL)
                        TI=2;
                if(TI==0 && SI==1){
                        SI=0;
                        read(sc);
                }
                else if(TI==0 && SI==2){
                        SI=0;
                        write(sc);
                }
                else if(TI==0 && SI==3){
                        terminate("Process exited with status 0",sc);
                }

                else if(TI==2 && SI==1){
                        terminate("Time Limit Exceeded",sc);
                }
                else if(TI==2 && SI==2){
                        write(sc);
                        terminate("Time Limit Exceeded",sc);
                }
                else if(TI==2 && SI==3){
                        terminate("Process exited with status 0",sc);
                }



                else if(TI==0 && PI==1){
                        terminate("Opcode Error",sc);
                }
                else if(TI==0 && PI==2){
                        terminate("Operand Error",sc);
                }
                else if(TI==0 && PI==3){
                        if(calledByRead==1){
                                calledByRead=0;
                                int frameToFill=allocate();
                                System.out.println("Frame assigned while handling valid page fault caused by GD : "+frameToFill);
                                String idk=Integer.toString(frameToFill);
                                if(frameToFill<10)
                                        idk="0"+idk;
                                char[] frameAsChar=idk.toCharArray();
                                memory[pageTableAT][0]='P';
                                memory[pageTableAT][1]=(char)pageNumber;
                                pageNumber++;
                                memory[pageTableAT][2]=frameAsChar[0];
                                memory[pageTableAT][3]=frameAsChar[1];
                                frameAssignedDuringValid=frameToFill;
                                TLC++;
                                PI=0;
                        }
                        else if(calledBySR==1){
                                calledBySR=0;
                                int frameToFill=allocate();
                                System.out.println("Frame assigned while handling valid page fault caused by SR : "+frameToFill);
                                String idk=Integer.toString(frameToFill);
                                if(frameToFill<10)
                                        idk="0"+idk;
                                char[] frameAsChar=idk.toCharArray();
                                memory[pageTableAT][0]='P';
                                memory[pageTableAT][1]=(char)pageNumber;
                                pageNumber++;
                                memory[pageTableAT][2]=frameAsChar[0];
                                memory[pageTableAT][3]=frameAsChar[1];
                                frameAssignedDuringValid=frameToFill;
                                TLC++;
                                PI=0;
                        }
                        else{
                                terminate("Invalid Page Fault",sc);
                        }
                }

                else if(TI==2 && PI==1){
                        terminate("Time Limit Exceeded\nOpcode Error",sc);
                }
                else if(TI==2 && PI==2){
                        terminate("Time Limit Exceeded\nOperand Error",sc);
                }
                else if(TI==2 && PI==3){
                        terminate("Time Limit Exceeded\nInvalid Page Fault",sc);
                }
        }

        public static void read(Scanner sc) throws Exception{
                String data=sc.nextLine();
                data=data.trim();
                System.out.println("Data read : "+data);
                String subdata;
                if(data.length()>4)
                        subdata=data.substring(0,4);
                else
                        subdata=data;
                if(subdata.equals("$END")){
                        errorOutOfData=1;
                        terminate("Out of data",sc);
                }
                else{
                        char[] dataChar=data.toCharArray();
                        int length=dataChar.length;
                        String temp2=""+IR[2]+IR[3];
                        calledByRead=1;
                        int block=addressTranslate(temp2,sc);
                        int counterForArray=0;
                        int counterForMemory=0;
                        while(counterForArray!=length){
                                if(dataChar[counterForArray]==' ' && counterForMemory!=0){
                                        block++;
                                        counterForArray++;
                                        counterForMemory=((counterForMemory/4)+1)*4;
                                }
                                else if(counterForMemory%4==0 && counterForMemory!=0){
                                        block++;
                                }
                                memory[block][counterForMemory%4]=dataChar[counterForArray];
                                counterForArray++;
                                counterForMemory++;
                        }
                }
        }

        public static void write(Scanner sc) throws Exception{
                LLC++;
                pro.linesPrinted=LLC;
                if(LLC>TLL){
                        errorLineLimit=1;
                        terminate("Line Limit Exceeded",sc);
                }
                else{
                        String temp=""+IR[2]+IR[3];
                        int block=addressTranslate(temp,sc);
                        if(block!=-1 && errorOutOfData!=1){
                                System.out.println("Writing from block : "+block+" to output file");
                                int i,j;
                                String dataToWrite="";
                                for(i=block; i<block+10; i++){
                                        for(j=0; j<4; j++){
                                                if(memory[i][j]!='_'){
                                                        dataToWrite=dataToWrite+memory[i][j];
                                                }
                                        }
                                        if(memory[i][j-1]=='_')
                                                dataToWrite=dataToWrite+" ";
                                }
                                FileWriter fw=new FileWriter("output.txt",true);
                                dataToWrite=dataToWrite+"\n";
                                fw.write(dataToWrite);
                                fw.close();
                        }
                }
        }

        public static void terminate(String message, Scanner sc) throws Exception{
                FileWriter fw=new FileWriter("output.txt",true);
                String temp="SI : "+SI+"  PI : "+PI+"  TI : "+TI;
		if(errorOutOfData==1){
			fw.write(message+"\n"+temp+"\n\n\n");
			IC=numberOfInstructions;
		}
		else if(errorLineLimit==1 || PI!=0 || TI==2){
                        fw.write(message+"\n"+temp+"\n\n\n");
                        String card;
                        String subcard;
                        do{
                                card=sc.nextLine();
                                if(card.length()>4)
                                        subcard=card.substring(0,4);
                                else
                                        subcard=card;
                                IC=numberOfInstructions;
                        }while(!subcard.equals("$END"));
                }
                else
                        fw.write(message+"\n"+temp+"\n\n\n");
                fw.close();
                System.out.println("===========PCB==========");
                System.out.println("Process ID : "+pro.processID);
                System.out.println("Total Time Limit : "+pro.totalTimeLimit);
                System.out.println("Total Line Limit : "+pro.totalLineLimit);
                System.out.println("Time Given : "+pro.completionTime);
                System.out.println("Lines Printed : "+pro.linesPrinted);
                System.out.println("Current Status : "+pro.currentStatus);
                System.out.println("\n\n\n\n\n\n");
        }
}
