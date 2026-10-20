---
name: Obsidian Algorithmic Terminal
colors:
  surface: '#0d131f'
  surface-dim: '#0d131f'
  surface-bright: '#333946'
  surface-container-lowest: '#080e1a'
  surface-container-low: '#161c28'
  surface-container: '#1a202c'
  surface-container-high: '#242a37'
  surface-container-highest: '#2f3542'
  on-surface: '#dde2f3'
  on-surface-variant: '#bac9cc'
  inverse-surface: '#dde2f3'
  inverse-on-surface: '#2a303d'
  outline: '#849396'
  outline-variant: '#3b494c'
  surface-tint: '#00daf3'
  primary: '#c3f5ff'
  on-primary: '#00363d'
  primary-container: '#00e5ff'
  on-primary-container: '#00626e'
  inverse-primary: '#006875'
  secondary: '#b2ffca'
  on-secondary: '#00391e'
  secondary-container: '#00f090'
  on-secondary-container: '#00683b'
  tertiary: '#ffe7e7'
  on-tertiary: '#680017'
  tertiary-container: '#ffc1c2'
  on-tertiary-container: '#b4002f'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#9cf0ff'
  primary-fixed-dim: '#00daf3'
  on-primary-fixed: '#001f24'
  on-primary-fixed-variant: '#004f58'
  secondary-fixed: '#58ffa5'
  secondary-fixed-dim: '#00e388'
  on-secondary-fixed: '#00210f'
  on-secondary-fixed-variant: '#00522e'
  tertiary-fixed: '#ffdada'
  tertiary-fixed-dim: '#ffb3b4'
  on-tertiary-fixed: '#40000b'
  on-tertiary-fixed-variant: '#920024'
  background: '#0d131f'
  on-background: '#dde2f3'
  surface-variant: '#2f3542'
typography:
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.03em
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.02em
  headline-sm:
    fontFamily: Space Grotesk
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: -0.01em
  title-md:
    fontFamily: Geist
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 22px
  title-sm:
    fontFamily: Geist
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
  body-lg:
    fontFamily: Geist
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 22px
  body-md:
    fontFamily: Geist
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
  body-sm:
    fontFamily: Geist
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  data-lg:
    fontFamily: JetBrains Mono
    fontSize: 20px
    fontWeight: '700'
    lineHeight: 26px
    letterSpacing: -0.01em
  data-md:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 18px
  data-sm:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
  label-md:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.04em
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 10px
    fontWeight: '600'
    lineHeight: 12px
    letterSpacing: 0.06em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  gutter: 0.75rem
  gutter-compact: 0.5rem
  margin: 1rem
  margin-sm: 0.75rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.75rem
  space-lg: 1rem
  space-xl: 1.5rem
---

## Brand & Style

The design system establishes a high-performance, dark-mode algorithmic perpetual futures trading experience tailored for mobile screens. Built to handle dense financial telemetry without visual exhaustion, it fuses modern technical minimalism with refined glassmorphism and surgical precision. 

The aesthetic radiates institutional authority, execution speed, and technological sophistication. It targets disciplined quantitative traders, algorithmic developers, and high-volume crypto futures specialists who require rapid situational awareness, absolute numerical legibility, and zero-latency visual feedback. 

Visual priorities are governed by utility:
- Obsidian surfaces recede to allow mission-critical data streams, candlestick patterns, and algorithmic telemetry to surge forward.
- High-frequency metrics (tick values, depth bars, PnL fluctuations) leverage razor-sharp signal colors (neon emerald, laser ruby, electric cyan) against slate backdrops to ensure zero cognitive ambiguity during volatile market swings.
- Form structures remain hyper-compact yet ergonomically optimized for single-thumb mobile interaction, combining tactile control modules with ambient glassmorphism panels.

## Colors

The palette is engineered specifically for deep-contrast, high-readability OLED/AMOLED mobile execution environments.

### Core Architecture
- **Surface Canvas (Base)**: `#0B0E14` (Deep void obsidian, eliminating glare and framing execution layers).
- **Surface Layer 1 (Cards & Modules)**: `#121824` (Tempered dark slate base for primary modular grouping).
- **Surface Layer 2 (Raised & Modals)**: `#1A2234` (Elevated slate for sheets, interactive dropdowns, and overlays).
- **Surface Interactive / Hover**: `#243048` (Selected states, pressed list items, active table rows).

### Primary Accents & Signals
- **Primary Accent (`#00E5FF`)**: Electric Cyan. Signifies active controls, focus rings, algorithmic bot triggers, selected navigation tabs, and system-level alerts.
- **Secondary Accent (`#00B4D8`)**: Oceanic Teal. Supports primary actions, secondary metric charts, and subdued technical indicators.
- **Bullish / Profit (`#00F090`)**: Neon Emerald. Dedicated exclusively to positive PnL, bid volumes, long positions, and buy order books. Subdued variant: `#10B981`.
- **Bearish / Loss (`#FF3B57`)**: Laser Crimson. Dedicated exclusively to negative PnL, ask volumes, short positions, liquidation levels, and stop-loss triggers. Subdued variant: `#EF4444`.
- **Warning / State Alert (`#FFB020`)**: High-saturation Amber. Used for margin warnings, high funding rate flags, network slippage, and connection states.

### Neutrals & Text Hierarchy
- **Text High-Contrast**: `#F8FAFC` (Pure balance, used for primary figures, tickers, and critical values).
- **Text Medium-Contrast**: `#94A3B8` (Cool slate, used for labels, field names, and passive data).
- **Text Low-Contrast / Disabled**: `#475569` (Muted slate, for secondary timestamps, table grid headers, and disabled actions).
- **Structural Outlines**: `rgba(255, 255, 255, 0.08)` to `rgba(0, 229, 255, 0.16)`.

## Typography

The typographic hierarchy enforces a dual-character framework:
1. **Space Grotesk** commands structural headers, modal titles, and navigation states with technical, modern geometry.
2. **Geist** powers body text, system messages, and control labels, delivering clarity at small viewport scales.
3. **JetBrains Mono** is enforced across all quantitative values, order book depths, timestamp counters, financial indicators, and execution parameters. Monospaced tabular alignment guarantees numbers never jitter or shift horizontal layouts during high-frequency live websocket updates.

### Usage Standards
- Always format price ticks, leverage multiples, position sizes, and funding rates with `data-*` or `label-*` monospaced tokens.
- Apply uppercase styling with slight positive letter spacing (`letterSpacing: 0.06em`) to `label-sm` tokens when displaying contract codes (e.g., `BTC-USDT-PERP`), order types (`LIMIT`, `POST-ONLY`), and indicator identifiers (`EMA(20)`, `RSI(14)`).

## Layout & Spacing

This design system utilizes an ultra-compact fluid grid optimized for high-density, real-time algorithmic telemetry on Android touchscreens.

### Structural Parameters
- **Base Grid**: 4px baseline sub-grid. All paddings, row heights, and layout gaps must resolve to multiples of 4px.
- **Mobile Fluid Layout**: 4-column fluid layout with an outer canvas margin of `1rem` (16px), down-scaling to `0.75rem` (12px) in condensed order-book split modes.
- **Component Gutters**: Standard gap of `0.75rem` (12px) between modular cards; condensed gap of `0.5rem` (8px) between co-dependent widgets (e.g., bid/ask order book splits, dual metric counters).
- **Vertical Density Rhythm**: Compact vertical margins (`0.25rem` to `0.5rem`) are enforced between telemetry rows to maximize above-the-fold visibility of live chart trends, active bots, and position tables.

### Mobile Execution Constraints
- Touch targets for core execution triggers (Buy/Long, Sell/Short, Emergency Close) maintain a minimum hit area of 44px vertical height, even when visual button padding is restricted to high-density thresholds.
- The viewport strictly enforces bottom safe-area offsets to seamlessly cradle the 5-tab fixed navigation bar without clipping trade-entry drawers.

## Elevation & Depth

Visual depth is achieved through layered glassmorphism, low-opacity technical outlines, and cold luminescent halos rather than heavy drop shadows.

### Layer Tiers
- **Tier 0 (Root Canvas)**: Solid `#0B0E14`. Houses canvas charts, grid backgrounds, and base workspace surfaces.
- **Tier 1 (Base Cards & Modules)**: Background `#121824` rendered with 85% opacity, `backdrop-filter: blur(16px)`, bordered by a 1px solid rim of `rgba(255, 255, 255, 0.07)`.
- **Tier 2 (Elevated Sheets & Floating Panels)**: Background `#1A2234` at 92% opacity, `backdrop-filter: blur(24px)`, framed by `rgba(0, 229, 255, 0.14)`. Accompanied by a diffused ambient shadow: `0 8px 32px -4px rgba(0, 0, 0, 0.60)`.
- **Tier 3 (Active Overlays, Modals, System Toasts)**: High-opacity `#1A2234` (98%), bounded by a 1px high-contrast cyan or emerald hairline boundary (`rgba(0, 229, 255, 0.35)`), with directional glows reserved exclusively for critical status confirmation.

### Glow & Luminous Signals
- Shadows are avoided on neutral surfaces. Active execution buttons and positive/negative status indicators project tight, subtle colored halos:
  - Bullish Active: `0 0 16px rgba(0, 240, 144, 0.25)`
  - Bearish Active: `0 0 16px rgba(255, 59, 87, 0.25)`
  - Algorithmic System Active: `0 0 14px rgba(0, 229, 255, 0.30)`

## Shapes

The design system adopts a **Soft (1)** shape language:
- Standard elements (cards, text fields, order buttons, table containers) feature `0.25rem` (4px) corner radii.
- Medium elements (`rounded-lg`, such as bottom sheets, modal alerts, and execution panels) utilize `0.5rem` (8px).
- Specialized overlay wrappers (`rounded-xl`) max out at `0.75rem` (12px).

### The Pill Exception
Status badges, position tags, leverage multipliers (e.g., `20x`), strategy indicators (e.g., `GRID ACTIVE`), and filter toggles are explicitly set to **full pill styling** (`border-radius: 9999px`). This intentional contrast separates continuous data surfaces and structured numeric grids from interactive or state-dependent metadata.

## Components

### Action Buttons
- **Buy / Long Action**: Solid `#00F090` foreground or fill with deep obsidian `#0B0E14` typography. Bold weight, high impact. Hover/active states project a luminous green ambient aura.
- **Sell / Short Action**: Solid `#FF3B57` foreground or fill with pure `#F8FAFC` typography. Hover/active states project a laser crimson aura.
- **Algorithmic Utility / Secondary**: Slate base `#1A2234` with 1px border of `rgba(0, 229, 255, 0.3)` and `#00E5FF` typography.
- **Ghost Actions**: Borderless, `Geist` medium text colored in `#94A3B8`, transitioning to `#00E5FF` upon activation.

### Status Chips & Pills
- Dimension: Ultra-compact (height: 20px–24px), horizontal padding of 8px.
- Styling: Rounded full (`9999px`). Translucent backgrounds (12% color opacity) paired with 1px border at 30% color opacity:
  - Long: Background `rgba(0, 240, 144, 0.12)`, text `#00F090`, border `rgba(0, 240, 144, 0.3)`.
  - Short: Background `rgba(255, 59, 87, 0.12)`, text `#FF3B57`, border `rgba(255, 59, 87, 0.3)`.
  - Bot Run State: Background `rgba(0, 229, 255, 0.12)`, text `#00E5FF`, border `rgba(0, 229, 255, 0.3)`. Includes a 4px pulsing dot icon.

### Form Inputs & Numeric Steppers
- Background: `#0B0E14` sunken interior, 1px perimeter border of `rgba(255, 255, 255, 0.12)`.
- Active / Focus: Border shifts to `#00E5FF` accompanied by a `0 0 0 1px #00E5FF` internal ring.
- Numeric Display: Right-aligned `JetBrains Mono` figures with dedicated fixed currency denotation (`USDT`) in `#475569`.
- Stepper Buttons (`+` / `-`): Inset squares of 28x28px in `#1A2234`, providing distinct mechanical haptics.

### Data Lists & Order Book Grids
- Density: Extreme vertical compaction (row heights of 22px to 28px).
- Font: `JetBrains Mono` sm/xs (`11px` / `12px`).
- Background Telemetry: Order depth visualizations use horizontal bar fills anchored to container edges:
  - Bids: `rgba(0, 240, 144, 0.08)` spanning right-to-left.
  - Asks: `rgba(255, 59, 87, 0.08)` spanning right-to-left.
- Dividers: Non-existent or ultra-subdued `rgba(255, 255, 255, 0.03)` hairline borders.

### Terminal Cards
- Structural frame: `#121824` background with frosted 12px blur, wrapped in 1px `rgba(255, 255, 255, 0.06)` outline.
- Header Zone: Integrated flex row carrying the primary asset icon, contract descriptor (`BTC-USDT`), 24h delta percentage chip, and mini timeframe switcher (`1m`, `5m`, `15m`, `1h`).

### Bottom Navigation Bar (5-Tab Anchor)
- Container: Persistent docked mobile navigation at screen base. Background `#0B0E14` (96% opacity) with `backdrop-filter: blur(20px)` and a top boundary rule of 1px `rgba(255, 255, 255, 0.08)`.
- Layout: 5 equal slots housing:
  1. **Chart**: Candlestick telemetry & technical overlays.
  2. **Strategy**: Quantitative rule builders & signal triggers.
  3. **Auto Trade**: Active bots, algorithmic engine status, emergency kill-switch.
  4. **Screener**: Real-time market volatility scanner, liquidation heatmap.
  5. **Menu**: API credentials, margin modes, latency telemetry, settings.
- Active State: Active tab icon and micro-label illuminate in `#00E5FF` with a subtle top glow bead (2px cyan line). Inactive tabs rest at `#475569`.
- Height: 60px + mobile safe bottom inset area.

### Technical Indicators Styling
- Candlesticks: Bullish hollow/filled `#00F090`, Bearish filled `#FF3B57`. Thin 1px wick rendering.
- Moving Averages: Cyan (`#00E5FF`, fast EMA), Ocean Teal (`#00B4D8`, slow EMA), Amber (`#FFB020`, baseline trend).
- Crosshairs: Hairline `rgba(255, 255, 255, 0.35)` dashed line paired with black pill callouts displaying dynamic `JetBrains Mono` coordinates.