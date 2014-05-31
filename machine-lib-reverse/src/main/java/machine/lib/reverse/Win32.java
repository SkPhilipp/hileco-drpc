package machine.lib.reverse;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Win32 {

    public static final Kernel32 KERNEL_32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
    public static final Psapi PSAPI = (Psapi) Native.loadLibrary("Psapi", Psapi.class);

    public interface Kernel32 extends StdCallLibrary {

        boolean ReadProcessMemory(Pointer hProcess, int inBaseAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead);

        public Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);

        boolean WriteProcessMemory(Pointer hProcess, int AdressToChange, Pointer ValuesToWrite, int nSize, IntByReference irgendwas);

        int GetLastError();

        boolean EnumProcesses(int[] ProcessIDsOut, int size, int[] BytesReturned);

        int GetProcessImageFileNameW(Pointer Process, char[] outputname, int lenght);

    }

    public interface Psapi extends StdCallLibrary {

        boolean EnumProcesses(int[] ProcessIDsOut, int size, int[] BytesReturned);

        int GetProcessImageFileNameW(Pointer Process, char[] outputname, int lenght);

    }

}
