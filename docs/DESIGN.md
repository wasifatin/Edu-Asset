# UI/UX Design System Specification — EduAsset

## Modern Flat Administration Dashboard Architecture (Nimbra Reference)

---

## 1. Nimbra Visual Reference Analysis

EduAsset's user interface is modeled after the **Nimbra** administrative dashboard design system. The goal of this visual design is to eliminate the dated, generic "student assignment" aesthetic typical of legacy Swing programs, replacing it with a clean, modern desktop experience.

### Key Visual Characteristics
1. **Desktop-First Administrative Canvas:** Wide-aspect layout designed for 1440 × 900 px desktop screens with adaptive fluidity down to 1100 × 700 px.
2. **Collapsible Navigation Sidebar:** Left navigation bar toggling between an expanded 256 px view (icons + labels + section headers) and a compact 84 px icon view.
3. **Card-Based Visual Hierarchy:** Content grouped into rounded surface panels with subtle borders and shadows.
4. **Distinctive KPI Cards:** Top-level metrics grouped in cards with distinct accent colors (Royal Blue, Emerald Green, Amber, Crimson, Azure).
5. **Modern Top Navigation Bar:** 62 px high navbar hosting live search, contextual screen title, user profile pill, and one-click Light/Dark theme toggle.
6. **Polished Data Tables:** Clear headers, custom row heights (38 px), alternating row stripes, and row-level action triggers.

---

## 2. Color System & Theme Palettes

EduAsset implements runtime theme switching through `ThemeManager.java`. All UI components subscribe to the observer pattern and update colors when a theme switch occurs.

### 2.1 Light Theme Palette (Porcelain Clean)

| Semantic Role | Hex Code | RGB | Swing Representation | Usage |
|---|---|---|---|---|
| **Canvas Background** | `#E9EEF8` | `(233, 238, 248)` | `new Color(0xE9, 0xEE, 0xF8)` | Main application background |
| **Main Surface** | `#FFFFFF` | `(255, 255, 255)` | `new Color(0xFF, 0xFF, 0xFF)` | Cards, tables, sidebar, topbar |
| **Secondary Surface** | `#F5F7FB` | `(245, 247, 251)` | `new Color(0xF5, 0xF7, 0xFB)` | Input fields, alternating table rows |
| **Primary Text** | `#0D1630` | `(13, 22, 48)` | `new Color(0x0D, 0x16, 0x30)` | High-contrast headers and table data |
| **Secondary Text** | `#55617C` | `(85, 97, 124)` | `new Color(0x55, 0x61, 0x7C)` | Subtitles, field labels, timestamps |
| **Primary Accent** | `#2F5CF0` | `(47, 92, 240)` | `new Color(0x2F, 0x5C, 0xF0)` | Primary buttons, active nav, brand |
| **Success** | `#0E9A78` | `(14, 154, 120)` | `new Color(0x0E, 0x9A, 0x78)` | Pass badges, publish buttons, active |
| **Warning** | `#C27C0E` | `(194, 124, 14)` | `new Color(0xC2, 0x7C, 0x0E)` | Under review, late arrivals, caution |
| **Danger** | `#BE2639` | `(190, 38, 57)` | `new Color(0xBE, 0x26, 0x39)` | Fail status, cancellations, delete |
| **Information** | `#1563B5` | `(21, 99, 181)` | `new Color(0x15, 0x63, 0xB5)` | Transcript dialogs, room indicators |
| **Subtle Border** | `#D6DEEE` | `(214, 222, 238)` | `new Color(0xD6, 0xDE, 0xEE)` | Card borders, table dividers |

### 2.2 Dark Theme Palette (Obsidian Deep)

| Semantic Role | Hex Code | RGB | Swing Representation | Usage |
|---|---|---|---|---|
| **Canvas Background** | `#050A18` | `(5, 10, 24)` | `new Color(0x05, 0x0A, 0x18)` | Deep midnight canvas |
| **Main Surface** | `#101A36` | `(16, 26, 54)` | `new Color(0x10, 0x1A, 0x36)` | Elevated cards, tables, topbar |
| **Secondary Surface** | `#0B132A` | `(11, 19, 42)` | `new Color(0x0B, 0x13, 0x2A)` | Sidebar, input fields, dark stripes |
| **Primary Text** | `#EEF3FF` | `(238, 243, 255)` | `new Color(0xEE, 0xF3, 0xFF)` | High-contrast white/ice headers |
| **Secondary Text** | `#95A2C2` | `(149, 162, 194)` | `new Color(0x95, 0xA2, 0xC2)` | Muted subtitles and column labels |
| **Primary Accent** | `#3D6BFF` | `(61, 107, 255)` | `new Color(0x3D, 0x6B, 0xFF)` | Vibrant glowing primary blue |
| **Success** | `#4FE0A0` | `(79, 224, 160)` | `new Color(0x4F, 0xE0, 0xA0)` | Glowing green indicators |
| **Warning** | `#FBC35E` | `(251, 195, 94)` | `new Color(0xFB, 0xC3, 0x5E)` | Radiant amber alerts |
| **Danger** | `#FF7D8D` | `(255, 125, 141)` | `new Color(0xFF, 0x7D, 0x8D)` | Soft crimson error badges |
| **Information** | `#388BEA` | `(56, 139, 234)` | `new Color(0x38, 0x8B, 0xEA)` | Sky blue badges |
| **Subtle Border** | `#1B294D` | `(27, 41, 77)` | `new Color(0x1B, 0x29, 0x4D)` | Dark navy card borders |

---

## 3. Typography & Sizing

The system uses **Segoe UI** (with automatic fallback to system sans-serif):

| Level | Size | Weight | Line Height | Usage |
|---|---|---|---|---|
| **Display Title** | 44 pt | Bold | 52 px | Login Frame Brand Header |
| **Page Header** | 22–24 pt | Bold | 32 px | Dashboard Top Greeting |
| **Section Title** | 18 pt | Bold | 26 px | Panel Titles, Modal Titles |
| **Card Title** | 14–16 pt | Bold | 22 px | Table Card Headers, KPI Labels |
| **KPI Big Value** | 28 pt | Bold | 36 px | Large StatCard Metric Numbers |
| **Body Regular** | 13 pt | Plain | 18 px | Table Data, Form Fields, Paragraphs |
| **Caption / Meta** | 11–12 pt | Plain | 16 px | Card Subtitles, Section Headers |

---

## 4. Spacing System & Geometry

- `Constants.SPACING_SM` = 12 px (tight component grouping, button padding)
- `Constants.SPACING` = 16 px (standard layout gutters and card padding)
- `Constants.SPACING_LG` = 20 px (screen edge margins, dialog borders)
- `Constants.CARD_RADIUS` = 18 px (rounded card corners via `RoundRectangle2D`)
- `Constants.CONTROL_RADIUS` = 12 px (rounded buttons and input fields)
- `Constants.TOPBAR_HEIGHT` = 62 px
- `Constants.SIDEBAR_EXPANDED` = 256 px
- `Constants.SIDEBAR_COLLAPSED` = 84 px

---

## 5. Reusable Component Design Specifications

### 5.1 `RoundedPanel`
Extends `JPanel`. Overrides `paintComponent(Graphics g)`:
```java
Graphics2D g2 = (Graphics2D) g.create();
g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
g2.setColor(backgroundColor);
g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
g2.setColor(theme.getBorderColor());
g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
g2.dispose();
```

### 5.2 `StatCard`
Extends `RoundedPanel`. Visual layout:
- 4 px top accent bar painted in accent color (Blue, Green, Amber, Crimson).
- Small uppercase title (12 pt, secondary text).
- Large bold value (28 pt bold, primary text).
- Contextual subtitle (11 pt, secondary text).

### 5.3 `DataTablePanel`
Encapsulates `JTable` + `JScrollPane`:
- Header height: 36 px with accent/secondary background, bold text.
- Row height: 38 px with vertical centering.
- Alternating row background (`theme.getMainSurface()` vs `theme.getSecondarySurface()`).
- Integrated top search bar with `TableRowSorter` and regex filtering.

### 5.4 `CustomButton`
Extends `JButton`:
- Smooth rounded rectangle background with hover brighten (+25 RGB) and press darken (-30 RGB).
- Hand cursor on mouseover.
- Flat appearance with zero native border or focus ring.

### 5.5 `FormDialog`
Extends `JDialog`:
- Modal base with GridBagLayout form layout.
- Automatic styling for `JTextField`, `JComboBox`, and `JTextArea`.
- Standard bottom button panel aligned right (`Cancel` + `Confirm Action`).

---

## 6. Sidebar Navigation Architecture

### Sidebar Structure
- **Branding Header:** App logo circle + "EduAsset" title.
- **Collapse Toggle Button:** ◀ (expanded) / ▶ (collapsed) toggle button.
- **Grouped Nav Sections:**
  - `WORKSPACE`: Overview, Student Records, Departments & Courses.
  - `EXAMINATION`: Exam Scheduling, Rooms & Seating, Attendance, Invigilators.
  - `ACADEMIC`: Marks & Assessment, Results & Transcripts, Applications.
  - `INSIGHTS`: Reports & Analytics, Activity History.
  - `ADMINISTRATION`: User Management, Settings.
- **Active Navigation Indicator:** Full-width rounded card background with primary accent color (`#2F5CF0`) and white text.
- **Role Visibility Filter:** Role-ineligible links are automatically excluded during sidebar build.

---

## 7. Responsive Resizing Behavior

- **Base Resolution:** 1440 × 900 px (ideal for modern laptop/desktop displays).
- **Minimum Enforced Size:** 1100 × 700 px via `setMinimumSize()`.
- **CardLayout Elasticity:** Table cards and KPI grids expand horizontally using `GridBagLayout` and `GridLayout` with weight proportions.
- **Vertical Scroll Panes:** Dashboard and analytics views wrap content in custom `JScrollPane`s with smooth 16 px unit wheel scrolling.

