package machine.lib.reverse;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// TODO: way not functional, needs ReadProcessMemory byte search
public class ProcessReader {

    /**
     * see http://msdn.microsoft.com/en-us/library/windows/desktop/ms684880(v=vs.85).aspx
     */
    private static final int PROCESS_QUERY_INFORMATION = 0x0400;
    private static final int PROCESS_VM_READ = 0x0010;
    private static final int PROCESS_VM_WRITE = 0x0020;
    private static final int PROCESS_VM_OPERATION = 0x0008;
    private static final Logger LOG = LoggerFactory.getLogger(ProcessReader.class);

    public static Pointer findFirstProcess(String ProcessNameToFind) {
        int[] processIds = new int[1024];
        int[] bytesReturned = new int[1024];
        Arrays.fill(processIds, 0);
        Arrays.fill(bytesReturned, 0);
        try {
            Win32.PSAPI.EnumProcesses(processIds, processIds.length, bytesReturned);
        } catch (Exception e) {
            LOG.warn("Erred while enumerating processes.", e);
            return null;
        }
        for (int processId : processIds) {
            if (processId != 0) {
                try {
                    Pointer tempProcess = Win32.KERNEL_32.OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ | PROCESS_VM_WRITE | PROCESS_VM_OPERATION, false, processId);
                    char[] buffer = new char[1024];
                    int length = Win32.PSAPI.GetProcessImageFileNameW(tempProcess, buffer, buffer.length);
                    StringBuilder builder = new StringBuilder(length);
                    builder.append(buffer, 0, length);
                    if (builder.toString().contains(ProcessNameToFind)) {
                        return tempProcess;
                    }
                } catch (Exception e) {
                    LOG.warn("Erred while opening process and reading file name.", e);
                }
            }
        }
        return null;
    }

    public static int[] readMemory(Pointer process, int addressOffset, int length) {
        Memory outputBuffer = new Memory(length);
        boolean success = Win32.KERNEL_32.ReadProcessMemory(process, addressOffset, outputBuffer, length, null);
        if (success) {
            IntBuffer intBuffer = ByteBuffer.wrap(outputBuffer.getByteArray(0, length)).asIntBuffer();
            int[] array = new int[intBuffer.remaining()];
            intBuffer.get(array);
            return array;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        Pointer MyProcess = findFirstProcess("Tutorial");
        LOG.info("tutorial is at {}", MyProcess);
        Scanner sc = new Scanner(System.in);
        Map<Integer, Integer> map = new HashMap<>();
        int find = 0;
        while (find != -1) {
            find = sc.nextInt();
            final int step = 1024 * 1024;
            for (int offset = 0; offset < step * 1000; offset += step) {
                int[] values = readMemory(MyProcess, offset, step);
                if (values != null) {
                    for (int index = 0; index < values.length; index++) {
                        int item = values[index];
                        if (item == find) {
                            int key = offset + index;
                            Integer integer = map.get(key);
                            map.put(key, (integer == null ? 0 : integer) + 1);
                        }
                    }
                }
            }
            System.out.println(map.size());
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                System.out.println(entry.getKey() + " x " + entry.getValue());
            }

        }
    }

}