package overwatch.getPlugin.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.utils.TxtFile;

public class Check
        implements Listener
{
    private String Identifier;
    private String Name;
    private Overwatch overwatch;
    private boolean Enabled = true;
    private boolean BanTimer = false;
    private boolean Bannable = true;
    private boolean JudgementDay = false;
    private Integer MaxViolations = Integer.valueOf(5);
    private Integer ViolationsToNotify = Integer.valueOf(1);
    private Long ViolationResetTime = Long.valueOf(600000L);
    public Map<String, List<String>> DumpLogs = new HashMap();

    public Check(String Identifier, String Name, Overwatch overwatch)
    {
        this.Name = Name;
        this.overwatch = overwatch;
        this.Identifier = Identifier;
    }

    public void dumplog(Player player, String log)
    {
        if (!this.DumpLogs.containsKey(player.getName()))
        {
            List<String> logs = new ArrayList();
            logs.add(log);
            this.DumpLogs.put(player.getName(), logs);
        }
        else
        {
            ((List)this.DumpLogs.get(player.getName())).add(log);
        }
    }

    public void onEnable() {}

    public void onDisable() {}

    public boolean isEnabled()
    {
        return this.Enabled;
    }

    public boolean isBannable()
    {
        return this.Bannable;
    }

    public boolean hasBanTimer()
    {
        return this.BanTimer;
    }

    public boolean isJudgmentDay()
    {
        return this.JudgementDay;
    }

    public Overwatch getOverwatch()
    {
        return this.overwatch;
    }

    public Integer getMaxViolations()
    {
        return this.MaxViolations;
    }

    public Integer getViolationsToNotify()
    {
        return this.ViolationsToNotify;
    }

    public Long getViolationResetTime()
    {
        return this.ViolationResetTime;
    }

    public void setEnabled(boolean Enabled)
    {
        if (Enabled)
        {
            if (!isEnabled()) {
                this.overwatch.RegisterListener(this);
            }
        }
        else if (isEnabled()) {
            HandlerList.unregisterAll(this);
        }
        this.Enabled = Enabled;
    }

    public void setBannable(boolean Bannable)
    {
        this.Bannable = Bannable;
    }

    public void setAutobanTimer(boolean BanTimer)
    {
        this.BanTimer = BanTimer;
    }

    public void setMaxViolations(int MaxViolations)
    {
        this.MaxViolations = Integer.valueOf(MaxViolations);
    }

    public void setViolationsToNotify(int ViolationsToNotify)
    {
        this.ViolationsToNotify = Integer.valueOf(ViolationsToNotify);
    }

    public void setViolationResetTime(long ViolationResetTime)
    {
        this.ViolationResetTime = Long.valueOf(ViolationResetTime);
    }

    public void setJudgementDay(boolean JudgementDay)
    {
        this.JudgementDay = JudgementDay;
    }

    public String getName()
    {
        return this.Name;
    }

    public String getIdentifier()
    {
        return this.Identifier;
    }

    public String dump(final String player) {
        if (!this.DumpLogs.containsKey(player)) {
            return null;
        }
        final TxtFile file = new TxtFile(this.getOverwatch(), "/Dumps", String.valueOf(player) + "_" + this.getIdentifier());
        file.clear();
        for (final String Line : this.DumpLogs.get(player)) {
            file.addLine(Line);
        }
        file.write();
        return file.getName();
    }
}