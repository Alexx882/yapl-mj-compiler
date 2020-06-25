package MJ;
import java.io.*;

// This class can be used to decode MicroJava object files.
// Synopsis: java MJ.Decode <object_file_name>
public class Decode {

	public static void main(String[] arg) {
		if (arg.length == 0)
			System.out.println("-- no filename specified");
		else {
			try {
				DataInputStream s = new DataInputStream(new FileInputStream(arg[0]));
				if (s.readByte() != 'M' || s.readByte() != 'J')
					throw new InvalidObjectException("invalid magic marker");
				int codeSize = s.readInt();
				int dataSize = s.readInt();
				int startPC = s.readInt();
				byte[] code = new byte[codeSize];
				if (s.read(code, 0, codeSize) != codeSize)
					throw new InvalidObjectException("unexpected end of file while reading code segment");
				int[] data = new int[dataSize];
				for (int i=0; i < dataSize; i++) {
					data[i] = s.readInt();
				}
				System.out.println("code: codeSize=" + codeSize + " startPC=" + startPC);
				Decoder.decode(code, 0, codeSize);
				if (dataSize > 0) {
					System.out.println("static data: dataSize=" + dataSize);
					Decoder.decodeStaticData(data, 0, dataSize);
				} else {
					System.out.println("no static data!");
				}
			} catch (InvalidObjectException e) {
				System.out.println("-- invalid object file " + arg[0]);
				System.out.println("-- "+e.getMessage());
			} catch (EOFException e) {
				System.out.println("-- invalid object file " + arg[0]);
				System.out.println("-- unexpected end of file while reading data segment");
			} catch (IOException e) {
				System.out.println("-- could not open file " + arg[0]);
			}
		}
	}
}