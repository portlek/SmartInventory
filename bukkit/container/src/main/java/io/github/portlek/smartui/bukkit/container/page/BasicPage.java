/*
 * MIT License
 *
 * Copyright (c) 2020 Hasan Demirtaş
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.github.portlek.smartui.bukkit.container.page;

import io.github.portlek.smartui.Source;
import io.github.portlek.smartui.bukkit.container.*;
import io.github.portlek.smartui.bukkit.container.content.BasicInventoryContents;
import io.github.portlek.smartui.bukkit.container.event.PgCloseEvent;
import io.github.portlek.smartui.bukkit.container.event.PgInitEvent;
import io.github.portlek.smartui.bukkit.container.event.PgUpdateEvent;
import io.github.portlek.smartui.bukkit.container.event.abs.CloseEvent;
import io.github.portlek.smartui.bukkit.container.event.abs.PageEvent;
import io.github.portlek.smartui.source.BasicSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BasicPage implements Page {

    private final Source<InventoryContents> source = new BasicSource<>();

    private final Collection<Target<? extends PageEvent>> targets = new ArrayList<>();

    @NotNull
    private final SmartInventory inventory;

    // TODO Add a method to change the type of the inventory.
    @NotNull
    private final InventoryType type = InventoryType.CHEST;

    @NotNull
    private InventoryProvided provided;

    @NotNull
    private String title = "Smart Inventory";

    private int row = 1;

    private int column = 9;

    private long tick = 1L;

    private long startDelay = 1L;

    private boolean async = false;

    private boolean tickEnable = true;

    @NotNull
    private String id = "none";

    @NotNull
    private Predicate<CloseEvent> canClose = event -> true;

    @Nullable
    private Page parent;

    public BasicPage(@NotNull final SmartInventory inventory, @NotNull final InventoryProvided provided) {
        this.inventory = inventory;
        this.provided = provided;
    }

    public BasicPage(@NotNull final SmartInventory inventory) {
        this(inventory, InventoryProvided.EMPTY);
    }

    @Override
    public void notifyUpdate(@NotNull final InventoryContents contents) {
        this.accept(new PgUpdateEvent(contents));
        this.source.notifyTargets(contents);
    }

    @Override
    public void notifyUpdateForAll() {
        this.inventory.notifyUpdateForAll(this.provided.getClass());
    }

    @Override
    public void notifyUpdateForAllById() {
        this.inventory.notifyUpdateForAllById(this.id);
    }

    @Override
    public <T extends PageEvent> void accept(@NotNull final T event) {
        this.targets.stream()
            .filter(target -> target.getType().isAssignableFrom(event.getClass()))
            .map(target -> (Target<T>) target)
            .forEach(target -> target.accept(event));
    }

    @NotNull
    @Override
    public InventoryProvided provider() {
        return this.provided;
    }

    @NotNull
    @Override
    public Page provider(@NotNull final InventoryProvided provided) {
        this.provided = provided;
        return this;
    }

    @NotNull
    @Override
    public SmartInventory inventory() {
        return this.inventory;
    }

    @Override
    public long tick() {
        return this.tick;
    }

    @NotNull
    @Override
    public Page tick(final long tick) {
        this.tick = tick;
        return this;
    }

    @Override
    public long startDelay() {
        return this.startDelay;
    }

    @NotNull
    @Override
    public Page startDelay(final long startDelay) {
        this.startDelay = startDelay;
        return this;
    }

    @Override
    public boolean async() {
        return this.async;
    }

    @NotNull
    @Override
    public Page async(final boolean async) {
        this.async = async;
        return this;
    }

    @Override
    public boolean tickEnable() {
        return this.tickEnable;
    }

    @NotNull
    @Override
    public Page tickEnable(final boolean tickEnable) {
        this.tickEnable = tickEnable;
        return this;
    }

    @Override
    public int row() {
        return this.row;
    }

    @NotNull
    @Override
    public Page row(final int row) {
        this.row = row;
        return this;
    }

    @Override
    public int column() {
        return this.column;
    }

    @NotNull
    @Override
    public Page column(final int column) {
        this.column = column;
        return this;
    }

    @NotNull
    @Override
    public String title() {
        return this.title;
    }

    @NotNull
    @Override
    public Page title(@NotNull final String title) {
        this.title = title;
        return this;
    }

    @NotNull
    @Override
    public Page parent(@NotNull final Page parent) {
        this.parent = parent;
        return this;
    }

    @NotNull
    @Override
    public Optional<Page> parent() {
        return Optional.ofNullable(this.parent);
    }

    @NotNull
    @Override
    public Page id(@NotNull final String id) {
        this.id = id;
        return this;
    }

    @NotNull
    @Override
    public String id() {
        return this.id;
    }

    @NotNull
    @Override
    public <T extends PageEvent> Page target(@NotNull final Target<T> target) {
        this.targets.add(target);
        return this;
    }

    @NotNull
    @Override
    public Page canClose(@NotNull final Predicate<CloseEvent> predicate) {
        this.canClose = predicate;
        return this;
    }

    @Override
    public boolean canClose(@NotNull final CloseEvent predicate) {
        return this.canClose.test(predicate);
    }

    @Override
    public boolean checkBounds(final int row, final int column) {
        if (row >= 0) {
            return column >= 0;
        }
        if (row < this.row) {
            return column < this.column;
        }
        return false;
    }

    @NotNull
    @Override
    public Inventory open(@NotNull final Player player, final int page, @NotNull final Map<String, Object> properties) {
        this.close(player);
        final InventoryOpener opener = this.inventory.findOpener(this.type).orElseThrow(() ->
            new IllegalStateException("No opener found for the inventory type " + this.type.name()));
        this.source.subscribe(this.provided);
        final InventoryContents contents = new BasicInventoryContents(this, player);
        contents.pagination().page(page);
        properties.forEach(contents::setProperty);
        this.inventory.setContents(player, contents);
        this.accept(new PgInitEvent(contents));
        this.provider().init(contents);
        final Inventory inventory = opener.open(this, player);
        this.inventory.setContentsByInventory(inventory, contents);
        this.inventory.setPage(player, this);
        if (this.tickEnable()) {
            this.inventory.tick(player, this);
        }
        return inventory;
    }

    @Override
    public void close(@NotNull final Player player) {
        this.inventory.getContents(player)
            .map(PgCloseEvent::new)
            .ifPresent(this::accept);
        this.inventory.stopTick(player);
        this.inventory.removePage(player);
        this.inventory.removeContent(player);
        this.inventory.removeContentByInventory(player.getOpenInventory().getTopInventory());
        this.source.unsubscribe(this.provided);
        player.closeInventory();
    }

}