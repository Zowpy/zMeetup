package me.zowpy.meetup.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextBuilder {

    private final TextComponent textComponent;

    public TextBuilder() {
        this.textComponent = new TextComponent();
    }

    public TextBuilder(TextComponent textComponent) {
        this.textComponent = textComponent;
    }

    public TextBuilder setColor(ChatColor color) {
        textComponent.setColor(color);
        return this;
    }

    public TextBuilder setText(String text) {
        textComponent.setText(text);
        return this;
    }

    public TextBuilder setBold(boolean bold) {
        textComponent.setBold(bold);
        return this;
    }

    public TextBuilder setUnderline(boolean underline) {
        textComponent.setUnderlined(underline);
        return this;
    }

    public TextBuilder setClickEvent(ClickEvent clickEvent) {
        textComponent.setClickEvent(clickEvent);
        return this;
    }

    public TextBuilder setHoverEvent(HoverEvent hoverEvent) {
        textComponent.setHoverEvent(hoverEvent);
        return this;
    }

    public TextBuilder addExtra(TextComponent textComponent) {
        this.textComponent.addExtra(textComponent);
        return this;
    }

    public TextComponent build() {
        return this.textComponent;
    }
}
