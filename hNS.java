package com.example.hNS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;

import net.minecraftforge.fml.common.Mod;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.util.ChatComponentText;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.awt.Toolkit;



@Mod(modid = hNS.MODID, version = hNS.VERSION)
public class hNS
{
    public static final String MODID = "hypixel_Nick_Sniper";
    public static final String VERSION = "1.5";
    
    private boolean wasBookOpen = false;
    private int waitTicks = 0;

    @EventHandler

    
    public void init(FMLInitializationEvent event)
    {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (waitTicks > 0) {
            waitTicks--;
            return;
        }


        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        

        
        if (mc.currentScreen instanceof GuiScreenBook) {
            if (!wasBookOpen) {
                wasBookOpen = true;


                //for (Field f : GuiScreenBook.class.getDeclaredFields()) {
                //    System.out.println("Field: " + f.getName() + " type: " + f.getType());
                //}


                try {
                    for (Field f : GuiScreenBook.class.getDeclaredFields()) 
                    {
                        if (NBTTagList.class.isAssignableFrom(f.getType())) {
                            f.setAccessible(true);
                            NBTTagList pagesTagList = (NBTTagList) f.get(mc.currentScreen);                                                

                            for (int i = 0; i < pagesTagList.tagCount(); i++) {
                                String page = pagesTagList.getStringTagAt(i);
                                Matcher nick = Pattern.compile("§l(.*?)§r").matcher(page);


                                if (nick.find()) {
                                    Matcher hasNumber = Pattern.compile("([1-9_]|harper|louie|jacob|jay|luke|mayla|john|joe|tim|luka|nate|andre|sean|aiden|anna|tobi)").matcher(nick.group(1));
                                    if (!hasNumber.find() && nick.group(1).length() < 10) {
                                        java.awt.Toolkit.getDefaultToolkit().beep();
                                        mc.thePlayer.addChatMessage(new ChatComponentText("Nick found: " + nick.group(1) + " Len: " + nick.group(1).length()));
                                    } else {
                                        mc.thePlayer.addChatMessage(new ChatComponentText("Bad nick: " + nick.group(1)));
                                        waitTicks = 20;
                                        mc.displayGuiScreen(null);
                                        mc.thePlayer.sendChatMessage("/nick help setrandom");
                                        wasBookOpen = false;
                                    }
                                  
                                }
                                else {
                                    mc.thePlayer.addChatMessage(new ChatComponentText(page));
                                }

                                
                            }

                            break;
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            wasBookOpen = false;
        }
    }
}