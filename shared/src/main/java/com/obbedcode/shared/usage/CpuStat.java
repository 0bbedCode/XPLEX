package com.obbedcode.shared.usage;

public class CpuStat {
    /*
        Example of /proc/stat Output:
        Column Names:
        CPU  user     nice   system   idle      iowait   irq    softirq  steal  guest  guest_nice

        Scenario 1: 0% CPU Usage (Completely Idle)

        cpu  1000     100    500      998000    300      50     50       0      0      0
        cpu0 125      12     62       124750    37       6      6        0      0      0
        cpu1 125      12     62       124750    37       6      6        0      0      0
        cpu2 125      12     62       124750    37       6      6        0      0      0
        cpu3 125      12     62       124750    37       6      6        0      0      0
        cpu4 125      12     63       124750    38       6      6        0      0      0
        cpu5 125      13     63       124750    38       7      7        0      0      0
        cpu6 125      13     63       124750    38       7      7        0      0      0
        cpu7 125      14     63       124750    38       6      6        0      0      0

        Scenario 2: 25% CPU Usage

        cpu  250000   10000  40000    850000    8000     1000   1000     0      0      0
        cpu0 31250    1250   5000     106250    1000     125    125      0      0      0
        cpu1 31250    1250   5000     106250    1000     125    125      0      0      0
        cpu2 31250    1250   5000     106250    1000     125    125      0      0      0
        cpu3 31250    1250   5000     106250    1000     125    125      0      0      0
        cpu4 31250    1250   5000     106250    1000     125    125      0      0      0
        cpu5 31250    1250   5000     106250    1000     125    125      0      0      0
        cpu6 31250    1250   5000     106250    1000     125    125      0      0      0
        cpu7 31250    1250   5000     106250    1000     125    125      0      0      0

        Scenario 3: 90% CPU Usage

        cpu  900000   30000  170000   100000    20000    5000   5000     0      0      0
        cpu0 112500   3750   21250    12500     2500     625    625      0      0      0
        cpu1 112500   3750   21250    12500     2500     625    625      0      0      0
        cpu2 112500   3750   21250    12500     2500     625    625      0      0      0
        cpu3 112500   3750   21250    12500     2500     625    625      0      0      0
        cpu4 112500   3750   21250    12500     2500     625    625      0      0      0
        cpu5 112500   3750   21250    12500     2500     625    625      0      0      0
        cpu6 112500   3750   21250    12500     2500     625    625      0      0      0
        cpu7 112500   3750   21250    12500     2500     625    625      0      0      0
     */

    public int CoreIndex = -1;
    public long User;
    public long Nice;
    public long System;
    public long Idle;
    public long IoWait;
    public long Irq;
    public long Softirq;
    public long Steal;
    public long Guest;
    public long GuestNice;

    public CpuStat(long[] values) {
        if(values.length > 0) {
            for(int i = 0; i < values.length; i++) {
                long l = values[i];
                switch (i) {
                    case 0: User = l; break;
                    case 1: Nice = l; break;
                    case 2: System = l; break;
                    case 3: Idle = l; break;
                    case 4: IoWait = l; break;
                    case 5: Irq = l; break;
                    case 6: Softirq = l; break;
                    case 7: Steal = l; break;
                    case 8: Guest = l; break;
                    case 9: GuestNice = l; break;
                }
            }
        }
    }

    public boolean isOverallCpu() { return CoreIndex == -1; }
}
