package overwatch.getPlugin.utils;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class UtilActionMessage
{
    private List<AMText> Text = new ArrayList();

    public static enum ClickableType
    {
        RunCommand("run_command"),  SuggestCommand("suggest_command"),  OpenURL("open_url");

        public String Action;

        private ClickableType(String Action)
        {
            this.Action = Action;
        }
    }

    public class AMText
    {
        private String Message = "";
        private Map<String, Map.Entry<String, String>> Modifiers = new HashMap();

        public AMText(String Text)
        {
            this.Message = Text;
        }

        public String getMessage()
        {
            return this.Message;
        }

        public String getFormattedMessage()
        {
            String Chat = "{\"text\":\"" + this.Message + "\"";
            for (String Event : this.Modifiers.keySet())
            {
                Map.Entry<String, String> Modifier = (Map.Entry)this.Modifiers.get(Event);
                Chat = Chat + ",\"" + Event + "\":{\"action\":\"" + (String)Modifier.getKey() + "\",\"value\":" + (String)Modifier.getValue() + "}";
            }
            Chat = Chat + "}";
            return Chat;
        }

        public AMText addHoverText(String... Text)
        {
            String Event = "hoverEvent";
            String Key = "show_text";
            String Value = "";
            if (Text.length == 1)
            {
                Value = "{\"text\":\"" + Text[0] + "\"}";
            }
            else
            {
                Value = "{\"text\":\"\",\"extra\":[";
                String[] arrayOfString;
                int j = (arrayOfString = Text).length;
                for (int i = 0; i < j; i++)
                {
                    String Message = arrayOfString[i];
                    Value = Value + "{\"text\":\"" + Message + "\"},";
                }
                Value = Value.substring(0, Value.length() - 1);
                Value = Value + "]}";
            }
            Map.Entry<String, String> Values = new AbstractMap.SimpleEntry(Key, Value);
            this.Modifiers.put(Event, Values);
            return this;
        }

        public AMText addHoverItem(org.bukkit.inventory.ItemStack Item)
        {
            String Event = "hoverEvent";
            String Key = "show_item";
            String Value = CraftItemStack.asNMSCopy(Item).getTag().toString();
            Map.Entry<String, String> Values = new AbstractMap.SimpleEntry(Key, Value);
            this.Modifiers.put(Event, Values);
            return this;
        }

        public AMText setClickEvent(UtilActionMessage.ClickableType Type, String Value)
        {
            String Event = "clickEvent";
            String Key = Type.Action;
            Map.Entry<String, String> Values = new AbstractMap.SimpleEntry(Key, "\"" + Value + "\"");
            this.Modifiers.put(Event, Values);
            return this;
        }
    }

    public AMText addText(String Message)
    {
        AMText Text = new AMText(Message);
        this.Text.add(Text);
        return Text;
    }

    public String getFormattedMessage()
    {
        String Chat = "[\"\",";
        for (AMText Text : this.Text) {
            Chat = Chat + Text.getFormattedMessage() + ",";
        }
        Chat = Chat.substring(0, Chat.length() - 1);
        Chat = Chat + "]";
        return Chat;
    }

    public void sendToPlayer(Player Player)
    {
        IChatBaseComponent base = ChatSerializer.a(getFormattedMessage());
        PacketPlayOutChat packet = new PacketPlayOutChat(base, 1);
        ((CraftPlayer)Player).getHandle().playerConnection.sendPacket(packet);
    }
}

