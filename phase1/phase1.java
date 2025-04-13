import java.io.*;
import java.util.*;
class phase1{
	public static char[][] memory=new char[100][4];
	public static int IC;
	public static char[] R=new char[4];
	public static char[] IR=new char[4];
	public static int SI;
	public static int C;
	public static char[] buffer=new char[40];
	public static int numberOfInstructions;
	public static void main(String[] args) throws Exception{
		int i;
		load();
	}

	public static void displayMemoryContents(){
		int i;
		for(i=0; i<100; i++){
			if(i%10==0 && i!=0)
				System.out.println("-----------------------------------------------------------------");
			System.out.println(i+"	"+memory[i][0]+memory[i][1]+memory[i][2]+memory[i][3]);
		}
		System.out.println();
		System.out.println();
		System.out.println();
	}
	
	public static void load() throws Exception{
		String line;
		int i;
		String subline;
		int linePointer=0;
		int insCounter=0;
		File f=new File("input.txt");
		Scanner sc=new Scanner(f);
		while(sc.hasNextLine()){
			line=sc.nextLine();
			if(line.length()>4)
				subline=line.substring(0,4);
			else
				subline=line;
			if(subline.equals("$AMJ")){
				linePointer=0;
				insCounter=0;
				numberOfInstructions=Integer.parseInt(line.substring(8,12));
				initialize();
			}
			else if(subline.equals("$DTA")){
				executeProgram(sc);
			}
			else if(subline.equals("$END")){
				displayMemoryContents();
				continue;
			}
			else{
				line=line.trim();
				char[] instructions=line.toCharArray();
				for(i=0; i<instructions.length; i++)
					buffer[i]=instructions[i];
				for(i=0; i<instructions.length; i++){
					if(insCounter!=0 && insCounter%4==0)
						linePointer++;
					if(instructions[i]=='H'){
						memory[linePointer][insCounter%4]=buffer[i];
						insCounter=((insCounter/4)+1)*4;
					}
					else{
						memory[linePointer][insCounter%4]=buffer[i];
						insCounter++;
					}
				}
				for(i=0; i<40; i++)
					buffer[i]='_';
			}
		}
	}

	public static void initialize() throws Exception{
		int i;
		IC=0;
		SI=0;
		C=0;
		R[0]='_';
		R[1]='_';
		R[2]='_';
		R[3]='_';
		IR[0]='_';
		IR[1]='_';
		IR[2]='_';
		IR[3]='_';
		for(i=0; i<100; i++){
			memory[i][0]='_';
			memory[i][1]='_';
			memory[i][2]='_';
			memory[i][3]='_';
		}
		for(i=0; i<40; i++)
			buffer[i]='_';
		File of=new File("output.txt");
		if(of.createNewFile())
			System.out.println("Created an output file.");
	}

	public static void executeProgram(Scanner sc) throws Exception{
		int i,address;
		String temporary;
		while(IC!=numberOfInstructions){
			temporary="";
			address=0;
			for(i=0; i<4; i++)
				IR[i]=memory[IC][i];
			if(IR[0]=='G' && IR[1]=='D'){
				IC++;
				IR[3]='0';
				SI=1;
				masterMode(sc);
			}
			else if(IR[0]=='P' && IR[1]=='D'){
				IC++;
				IR[3]='0';
				SI=2;
				masterMode(sc);
			}
			else if(IR[0]=='H'){
				IC++;
				SI=3;
				masterMode(sc);
				break;
			}
			else if(IR[0]=='L' && IR[1]=='R'){
				IC++;
				temporary=""+IR[2]+IR[3];
				address=Integer.parseInt(temporary);
				for(i=0; i<4; i++)
					R[i]=memory[address][i];
			}
			else if(IR[0]=='S' && IR[1]=='R'){
				IC++;
				temporary=""+IR[2]+IR[3];
				address=Integer.parseInt(temporary);
				for(i=0; i<4; i++)
					memory[address][i]=R[i];
			}
			else if(IR[0]=='C' && IR[1]=='R'){
				IC++;
				temporary=""+IR[2]+IR[3];
				address=Integer.parseInt(temporary);
				for(i=0; i<4; i++){
					if(R[i]==memory[address][i]){
						C=1;
					}
					else{
						C=0;
						break;
					}
				}
			}
			else if(IR[0]=='B' && IR[1]=='T'){
				if(C==1){
					temporary=""+IR[2]+IR[3];
					address=Integer.parseInt(temporary);
					IC=address;
				}
				else{
					IC++;
				}
			}
		}
	}

	public static void masterMode(Scanner sc) throws Exception{
		if(SI==1)
			read(sc);
		else if(SI==2)
			write();
		else if(SI==3)
			terminate();
	}

	public static void read(Scanner sc) throws Exception{
		String data=sc.nextLine();
		char[] dataChar=data.toCharArray();
		int length=dataChar.length;
		int block=Integer.parseInt(""+IR[2]+IR[3]);
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

	public static void write() throws Exception{
		int block=Integer.parseInt(""+IR[2]+IR[3]);
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

	public static void terminate() throws Exception{
		FileWriter fw=new FileWriter("output.txt",true);
		fw.write("\n\n");
		fw.close();
	}
}
