package djs.game.ball;

import java.io.Serializable;

public class CPlayer implements Serializable{
    // inner
    public abstract class CUpgrade implements Serializable{
        // constants
        private static final long serialVersionUID = 2L;

        // variables
        private int m_level;

        // functions
        public CUpgrade(int level){
            this.m_level = level;
        }

        public int get_level(){
            return this.m_level;
        }

        public void set_level(int level){
            this.m_level = level;
        }

        public int get_points_needed_for_upgrade(){
            // points = (next level)^2 + 15
            return (this.m_level + 2) * (this.m_level + 2) + 15;
        }

        public abstract float get_boost_percent();
    }
    public class CUpgradeBallsPerLevel extends CUpgrade{
        // variables


        // functions
        public CUpgradeBallsPerLevel(int level) {
            super(level);
        }

        @Override
        public float get_boost_percent() {
            switch (this.get_level()){
                case 0: { return 0.000f; }
                case 1: { return 0.050f; }
                case 2: { return 0.100f; }
                case 3: { return 0.150f; }
                case 4: { return 0.200f; }
                case 5: { return 0.250f; }
                case 6: { return 0.300f; }
                case 7: { return 0.350f; }
                case 8: { return 0.400f; }
                case 9: { return 0.450f; }
                case 10: { return 0.500f; }
            }
            return 0.0f;
        }
    }
    public class CUpgradeBombChance extends CUpgrade{
        // variables


        // functions
        public CUpgradeBombChance(int level) {
            super(level);
        }
        @Override
        public float get_boost_percent() {
            switch (this.get_level()){
                case 0: { return 0.0000f; }
                case 1: { return 0.0040f; }
                case 2: { return 0.0080f; }
                case 3: { return 0.0120f; }
                case 4: { return 0.0160f; }
                case 5: { return 0.0200f; }
                case 6: { return 0.0240f; }
                case 7: { return 0.0280f; }
                case 8: { return 0.0320f; }
                case 9: { return 0.0360f; }
                case 10: { return 0.0400f; }
            }
            return 0.0f;
        }
    }
    public class CUpgradePointChance extends CUpgrade{
        // variables


        // functions
        public CUpgradePointChance(int level) {
            super(level);
        }
        @Override
        public float get_boost_percent() {
            switch (this.get_level()){
                case 0: { return 0.0000f; }
                case 1: { return 0.0040f; }
                case 2: { return 0.0080f; }
                case 3: { return 0.0120f; }
                case 4: { return 0.0160f; }
                case 5: { return 0.0200f; }
                case 6: { return 0.0240f; }
                case 7: { return 0.0280f; }
                case 8: { return 0.0320f; }
                case 9: { return 0.0360f; }
                case 10: { return 0.0400f; }
            }
            return 0.0f;
        }
    }
    public class CUpgradeAimerLength extends CUpgrade{
        // variables


        // functions
        public CUpgradeAimerLength(int level) {
            super(level);
        }
        @Override
        public float get_boost_percent() {
            switch (this.get_level()){
                case 0: { return 0.0f; }
                case 1: { return 0.2f; }
                case 2: { return 0.4f; }
                case 3: { return 0.6f; }
                case 4: { return 0.8f; }
                case 5: { return 1.0f; }
                case 6: { return 1.2f; }
                case 7: { return 1.4f; }
                case 8: { return 1.6f; }
                case 9: { return 1.8f; }
                case 10: { return 2.0f; }
            }
            return 0.0f;
        }
    }

    // constants
    private static final long serialVersionUID = 1L;

    // variables
    private String m_name;
    private long m_current_points;
    private long m_total_points_earned;
    private long m_total_balls_launched;
    private long m_total_levels_completed;
    private long m_total_blocks_destroyed;
    private long m_total_games;
    private long m_current_game_points_earned;
    private long m_current_game_balls_launched;
    private long m_current_game_levels_completed;
    private long m_current_game_blocks_destroyed;
    private long m_highest_game_points_earned;
    private long m_highest_game_balls_launched;
    private long m_highest_game_levels_completed;
    private long m_highest_game_blocks_destroyed;
    private CUpgradeBallsPerLevel m_upgrade_balls_per_level;
    private CUpgradeBombChance m_upgrade_bomb_chance;
    private CUpgradePointChance m_upgrade_point_chance;
    private CUpgradeAimerLength m_upgrade_aimer_length;

    // functions
    public CPlayer(String name){
        this.m_name = name;
        this.m_current_points = 0;
        this.m_total_points_earned = 0;
        this.m_total_balls_launched = 0;
        this.m_total_levels_completed = 0;
        this.m_total_blocks_destroyed = 0;
        this.m_total_games = 0;
        this.m_current_game_points_earned = 0;
        this.m_current_game_balls_launched = 0;
        this.m_current_game_levels_completed = 0;
        this.m_current_game_blocks_destroyed = 0;
        this.m_highest_game_points_earned = 0;
        this.m_highest_game_balls_launched = 0;
        this.m_highest_game_levels_completed = 0;
        this.m_highest_game_blocks_destroyed = 0;
        this.m_upgrade_balls_per_level = new CUpgradeBallsPerLevel(0);
        this.m_upgrade_bomb_chance = new CUpgradeBombChance(0);
        this.m_upgrade_point_chance = new CUpgradePointChance(0);
        this.m_upgrade_aimer_length = new CUpgradeAimerLength(0);
    }

    public void set_name(String name){
        this.m_name = name;
    }

    public long get_total_games(){
        return this.m_total_games;
    }
    public long get_total_points_earned(){
        return this.m_total_points_earned;
    }
    public long get_total_blocks_destroyed(){
        return this.m_total_blocks_destroyed;
    }
    public long get_total_balls_launched(){
        return this.m_total_balls_launched;
    }
    public long get_total_levels_completed(){
        return this.m_total_levels_completed;
    }
    public long get_highest_game_points_earned() { return this.m_highest_game_points_earned; }
    public long get_highest_game_blocks_destroyed() { return this.m_highest_game_blocks_destroyed; }
    public long get_highest_game_balls_launched() { return this.m_highest_game_balls_launched; }
    public long get_highest_game_levels_completed() { return this.m_highest_game_levels_completed; }

    public long get_current_points(){
        return this.m_current_points;
    }
    public long get_current_game_points_earned() {
        return m_current_game_points_earned;
    }
    public long get_current_game_balls_launched() {
        return m_current_game_balls_launched;
    }
    public long get_current_game_levels_completed() {
        return m_current_game_levels_completed;
    }
    public long get_current_game_blocks_destroyed() {
        return m_current_game_blocks_destroyed;
    }

    public void set_current_game_points_earned(long current_game_points_earned) {
        this.m_current_game_points_earned = current_game_points_earned;
    }
    public void set_current_game_balls_launched(long current_game_balls_launched) {
        this.m_current_game_balls_launched = current_game_balls_launched;
    }
    public void set_current_game_levels_completed(long current_game_levels_completed) {
        this.m_current_game_levels_completed = current_game_levels_completed;
    }
    public void set_current_game_blocks_destroyed(long current_game_blocks_destroyed) {
        this.m_current_game_blocks_destroyed = current_game_blocks_destroyed;
    }

    public void adjust_total_games(long amount){
        this.m_total_games += amount;
    }
    public void adjust_current_points(long amount) {
        this.m_current_points += amount;
    }
    public void adjust_total_points_earned(long amount) {
        this.m_total_points_earned += amount;
    }
    public void adjust_total_balls_launched(long amount) {
        this.m_total_balls_launched += amount;
    }
    public void adjust_total_levels_completed(long amount) {
        this.m_total_levels_completed += amount;
    }
    public void adjust_total_blocks_destroyed(long amount) {
        this.m_total_blocks_destroyed += amount;
    }
    public void adjust_current_game_points_earned(long amount) {
        this.m_current_game_points_earned += amount;
    }
    public void adjust_current_game_balls_launched(long amount) {
        this.m_current_game_balls_launched += amount;
    }
    public void adjust_current_game_levels_completed(long amount) {
        this.m_current_game_levels_completed += amount;
    }
    public void adjust_current_game_blocks_destroyed(long amount) {
        this.m_current_game_blocks_destroyed += amount;
    }

    public CUpgrade get_balls_per_level_upgrade(){
        return this.m_upgrade_balls_per_level;
    }
    public CUpgrade get_bomb_chance_upgrade(){
        return this.m_upgrade_bomb_chance;
    }
    public CUpgrade get_point_chance_upgrade(){
        return this.m_upgrade_point_chance;
    }
    public CUpgrade get_aimer_length_upgrade(){
        return this.m_upgrade_aimer_length;
    }

    public void execute_game_over_calculations(){
        // check for new single game highs
        if (this.m_current_game_points_earned > this.m_highest_game_points_earned){
            this.m_highest_game_points_earned = this.m_current_game_points_earned;
        }
        if (this.m_current_game_balls_launched > this.m_highest_game_balls_launched){
            this.m_highest_game_balls_launched = this.m_current_game_balls_launched;
        }
        if (this.m_current_game_levels_completed > this.m_highest_game_levels_completed){
            this.m_highest_game_levels_completed = this.m_current_game_levels_completed;
        }
        if (this.m_current_game_blocks_destroyed > this.m_highest_game_blocks_destroyed){
            this.m_highest_game_blocks_destroyed = this.m_current_game_blocks_destroyed;
        }

        // reset the game stats
        this.m_current_game_blocks_destroyed = 0;
        this.m_current_game_balls_launched = 0;
        this.m_current_game_levels_completed = 0;
        this.m_current_game_points_earned = 0;
    }
}
