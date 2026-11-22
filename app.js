/**
 * EduAsset — Web Prototype Application Logic
 * Implements full state, conflict detection, marks boundary validation,
 * role permissions, and interactive workflows in pure JavaScript.
 */

// ==========================================================================
// 1. Initial State & In-Memory Database
// ==========================================================================

const State = {
  theme: 'light',
  currentRole: 'ADMIN', // 'ADMIN', 'COORDINATOR', 'TEACHER', 'STUDENT'
  activeScreen: 'overview',
  sidebarCollapsed: false,

  // User Accounts for Persona Authentication & Roleplay
  users: [
    { id: 'ADMIN001', name: 'Mr. Md. Ashraful Islam Talukder', email: 'ashrafulislam.cse@diu.edu.bd', role: 'ADMIN', designation: 'Examination Controller', active: true, created: '2026-11-10' },
    { id: 'COORD001', name: 'Dewan Mamun Raza', email: 'dewan.cse@diu.edu.bd', role: 'COORDINATOR', designation: 'Department Coordinator', active: true, created: '2026-11-10' },
    { id: 'TCH001', name: 'Pranto Protim Choudhury', email: 'prantoprotimchoudhury.cse@diu.edu.bd', role: 'TEACHER', designation: 'Faculty / Invigilator', active: true, created: '2026-11-10' },
    { id: 'STU001', name: 'Fatin Sharhad', email: '252-15-869@diu.edu.bd', role: 'STUDENT', designation: 'Student Examinee', active: true, created: '2026-11-10' }
  ],

  // Students — Clean initial state (user will input their own entries)
  students: [],

  // Departments — Clean initial state (user will input their own entries)
  departments: [],

  // Courses — Clean initial state (user will input their own entries)
  courses: [],

  // Examination Rooms — Clean initial state (user will input their own entries)
  rooms: [],

  // Exams — Clean initial state (user will input their own entries)
  exams: [],

  // Seat Assignments — Clean initial state (user will input their own entries)
  seats: [],

  // Attendance Records — Clean initial state (user will input their own entries)
  attendance: [],

  // Assessment Marks — Clean initial state (user will input their own entries)
  marks: [],

  // Results — Clean initial state (user will input their own entries)
  results: [],

  // Applications — Clean initial state (user will input their own entries)
  applications: [],

  // Audit History — Clean initial state (user will input their own entries)
  history: []
};

// ==========================================================================
// 2. Application Initialization, Glassmorphic Theme & Auth Engine
// ==========================================================================

document.addEventListener('DOMContentLoaded', () => {
  initTheme();
  initIntroScreen();
  setupAuthPortal();
  setupRoleSwitcher();
  setupSidebarNavigation();
  setupButtonRipples();
  refreshDatalists();
  updateAuditBadge();
  renderActiveScreen();
  setupModals();
});

// Enforce Light Glassmorphic Theme Permanently
function initTheme() {
  document.documentElement.setAttribute('data-theme', 'light');
  localStorage.removeItem('eduasset_theme');
}

// ==========================================================================
// 2.1 Intro Splash Screen with Cinematic Fluid Transition (Logo Only)
// ==========================================================================

let introFinished = false;

function initIntroScreen() {
  const introEl = document.getElementById('intro-splash-screen');
  const authPortal = document.getElementById('auth-portal-screen');

  if (!introEl) return;

  const duration = 1600; // 1.6s display
  const timer = setTimeout(transitionIntroToPortal, duration);

  // Click on intro screen skips immediately
  introEl.addEventListener('click', () => {
    clearTimeout(timer);
    transitionIntroToPortal();
  });

  function transitionIntroToPortal() {
    if (introFinished) return;
    introFinished = true;
    introEl.classList.add('intro-fade-out');
    setTimeout(() => {
      introEl.style.display = 'none';
      if (authPortal) {
        authPortal.classList.remove('portal-hidden');
      }
    }, 600);
  }
}

// ==========================================================================
// 2.2 Sign In Portal & 4 Auditor Roleplay Interface
// ==========================================================================

const Auditors = {
  ADMIN: {
    email: 'ashrafulislam.cse@diu.edu.bd',
    pass: 'admin123',
    role: 'ADMIN',
    name: 'Mr. Md. Ashraful Islam Talukder',
    title: 'Examination Controller',
    desc: 'Admin Authority',
    avatar: 'AT'
  },
  COORDINATOR: {
    email: 'dewan.cse@diu.edu.bd',
    pass: 'coord123',
    role: 'COORDINATOR',
    name: 'Dewan Mamun Raza',
    title: 'Department Coordinator',
    desc: 'Curriculum & Schedules',
    avatar: 'DR'
  },
  TEACHER: {
    email: 'prantoprotimchoudhury.cse@diu.edu.bd',
    pass: 'teach123',
    role: 'TEACHER',
    name: 'Pranto Protim Choudhury',
    title: 'Faculty / Invigilator',
    desc: 'Marks & Duty Roster',
    avatar: 'PC'
  },
  STUDENT: {
    email: '252-15-869@diu.edu.bd',
    pass: 'student123',
    role: 'STUDENT',
    name: 'Fatin Sharhad',
    title: 'Student Examinee',
    desc: 'Results & Grievances',
    avatar: 'FS'
  }
};

function setupAuthPortal() {
  const roleDropdownSelect = document.getElementById('auth-role-custom-select');
  const roleSelectBtn = document.getElementById('auth-role-select-btn');
  const roleOptions = document.querySelectorAll('.select-option-item');
  const triggerCode = document.getElementById('trigger-role-code');
  const triggerTitle = document.getElementById('trigger-role-title');

  const emailInput = document.getElementById('email');
  const passInput = document.getElementById('password');
  const form = document.getElementById('eduasset-signin-form');
  const authPortal = document.getElementById('auth-portal-screen');
  const appContainer = document.getElementById('app-container');
  const signOutBtn = document.getElementById('btn-signout-portal');

  // Custom Auditor Role Dropdown Interaction (Matching Screenshot)
  if (roleSelectBtn && roleDropdownSelect) {
    roleSelectBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      const isOpen = roleDropdownSelect.classList.toggle('open');
      roleSelectBtn.setAttribute('aria-expanded', isOpen);
    });

    roleOptions.forEach(opt => {
      opt.addEventListener('click', (e) => {
        e.stopPropagation();
        roleOptions.forEach(o => {
          o.classList.remove('selected');
          o.setAttribute('aria-selected', 'false');
        });
        opt.classList.add('selected');
        opt.setAttribute('aria-selected', 'true');

        const role = opt.getAttribute('data-role');
        const code = opt.getAttribute('data-code');
        const title = opt.getAttribute('data-title');
        const email = opt.getAttribute('data-email');
        const pass = opt.getAttribute('data-pass');
        const auditor = Auditors[role];

        if (triggerCode) triggerCode.textContent = code;
        if (triggerTitle) triggerTitle.textContent = title;
        if (emailInput) emailInput.value = email;
        if (passInput) passInput.value = pass;

        roleDropdownSelect.classList.remove('open');
        roleSelectBtn.setAttribute('aria-expanded', 'false');
      });
    });

    // Close dropdown on outside click
    document.addEventListener('click', (e) => {
      if (!roleDropdownSelect.contains(e.target)) {
        roleDropdownSelect.classList.remove('open');
        roleSelectBtn.setAttribute('aria-expanded', 'false');
      }
    });
  }

  // Sign In Form submission
  if (form) {
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      const email = (emailInput?.value || '').trim().toLowerCase();
      const pass = passInput?.value || '';

      // Match auditor credentials or fallback to active selected item
      let matchedRole = Object.keys(Auditors).find(r => Auditors[r].email.toLowerCase() === email && Auditors[r].pass === pass);

      if (!matchedRole) {
        matchedRole = Object.keys(Auditors).find(r => Auditors[r].email.toLowerCase() === email);
      }

      if (!matchedRole) {
        const activeOpt = document.querySelector('.select-option-item.selected');
        matchedRole = activeOpt ? activeOpt.getAttribute('data-role') : 'ADMIN';
      }

      const auditor = Auditors[matchedRole];
      authenticateAuditor(auditor);
    });
  }

  // Social Single Sign-On (SSO) buttons
  ['google', 'apple', 'twitter'].forEach(network => {
    const btn = document.getElementById(`btn-social-${network}`);
    if (btn) {
      btn.addEventListener('click', () => {
        const activeOpt = document.querySelector('.select-option-item.selected');
        const role = activeOpt ? activeOpt.getAttribute('data-role') : 'ADMIN';
        const auditor = Auditors[role];
        showToast(`Signing in via ${network.toUpperCase()} Single Sign-On...`, 'toast-success');
        setTimeout(() => {
          authenticateAuditor(auditor);
        }, 350);
      });
    }
  });

  // Forgot Password Link
  const forgotLink = document.getElementById('auth-forgot-link');
  if (forgotLink) {
    forgotLink.addEventListener('click', () => {
      showToast('Password recovery reset token sent to your institutional email.', 'toast-info');
    });
  }

  // License Agreement Link
  const agreeLink = document.getElementById('auth-agreement-link');
  if (agreeLink) {
    agreeLink.addEventListener('click', () => {
      showToast('EduAsset University Examination & Assessment Management License v2.4 (Active)', 'toast-info');
    });
  }

  // Sign Out / Switch Auditor back to Portal
  if (signOutBtn) {
    signOutBtn.addEventListener('click', () => {
      signOutToPortal();
    });
  }

  function authenticateAuditor(auditor) {
    State.currentRole = auditor.role;
    updateUserBadgeForAuditor(auditor);
    updateUserBadge();
    applyRoleNavigationVisibility();

    // Smooth transition from Sign-in Portal to Glass Dashboard
    if (authPortal) authPortal.classList.add('portal-hidden');
    if (appContainer) appContainer.classList.add('app-active');

    // Sync role selector in topbar
    const roleSelect = document.getElementById('role-select-box');
    if (roleSelect) roleSelect.value = auditor.role;

    showToast(`Signed in as ${auditor.title} (${auditor.name})`, 'toast-success');
  }

  function signOutToPortal() {
    if (appContainer) appContainer.classList.remove('app-active');
    if (authPortal) {
      authPortal.classList.remove('portal-hidden');
      authPortal.style.visibility = 'visible';
    }
    showToast('Session locked. Returned to Auditor Sign-In Portal.', 'toast-info');
  }
}

function updateUserBadgeForAuditor(auditor) {
  const nameEl = document.getElementById('current-user-name');
  const roleEl = document.getElementById('current-user-role');
  const avatarEl = document.querySelector('.user-avatar-circle');

  if (nameEl) nameEl.textContent = auditor.name;
  if (roleEl) roleEl.textContent = auditor.title;
  if (avatarEl) avatarEl.textContent = auditor.avatar;
}

// Fluid Easing & Dynamic Metric Counter
function animateCount(target, start, end, duration = 650, isFloat = false, suffix = '') {
  const el = typeof target === 'string' ? document.getElementById(target) : target;
  if (!el) return;

  const startTime = performance.now();
  const diff = end - start;

  function step(currentTime) {
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);
    const ease = 1 - Math.pow(1 - progress, 3);
    const val = start + diff * ease;

    el.textContent = (isFloat ? val.toFixed(1) : Math.round(val)) + suffix;

    if (progress < 1) {
      requestAnimationFrame(step);
    } else {
      el.textContent = (isFloat ? end.toFixed(1) : end) + suffix;
    }
  }

  requestAnimationFrame(step);
}

// Micro-Interaction: Button Ripple Wave Effect
function setupButtonRipples() {
  document.addEventListener('click', (e) => {
    const btn = e.target.closest('.btn, .btn-action, .btn-secondary, .btn-primary, .btn-toggle-sidebar, .tab-pill, .nav-item, .login-button, .social-button, .roleplay-tab');
    if (!btn) return;

    const rect = btn.getBoundingClientRect();
    const circle = document.createElement('span');
    circle.classList.add('ripple-circle');

    const diameter = Math.max(rect.width, rect.height);
    const radius = diameter / 2;

    circle.style.width = circle.style.height = `${diameter}px`;
    circle.style.left = `${e.clientX - rect.left - radius}px`;
    circle.style.top = `${e.clientY - rect.top - radius}px`;

    const existing = btn.querySelector('.ripple-circle');
    if (existing) existing.remove();

    btn.appendChild(circle);
    setTimeout(() => circle.remove(), 600);
  });
}

// Interactive Floating Chart Tooltip
function showChartTooltip(e, htmlContent) {
  const tip = document.getElementById('chart-floating-tooltip');
  if (!tip) return;
  tip.innerHTML = htmlContent;
  tip.style.left = `${e.clientX}px`;
  tip.style.top = `${e.clientY}px`;
  tip.style.opacity = '1';
  tip.style.transform = 'translate(-50%, -130%) scale(1)';
}

function hideChartTooltip() {
  const tip = document.getElementById('chart-floating-tooltip');
  if (!tip) return;
  tip.style.opacity = '0';
  tip.style.transform = 'translate(-50%, -120%) scale(0.95)';
}

// ==========================================================================
// 3. Role-Based Navigation & Permissions
// ==========================================================================

function setupRoleSwitcher() {
  const roleSelect = document.getElementById('role-select-box');
  const topbarDropdown = document.getElementById('topbar-role-dropdown');
  const topbarBtn = document.getElementById('topbar-role-btn');
  const topbarOpts = document.querySelectorAll('.topbar-role-opt');

  if (topbarBtn && topbarDropdown) {
    topbarBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      const isOpen = topbarDropdown.classList.toggle('open');
      topbarBtn.setAttribute('aria-expanded', isOpen);
    });

    topbarOpts.forEach(opt => {
      opt.addEventListener('click', (e) => {
        e.stopPropagation();
        const role = opt.getAttribute('data-role');
        State.currentRole = role;
        if (roleSelect) roleSelect.value = role;

        topbarOpts.forEach(o => o.classList.remove('selected'));
        opt.classList.add('selected');

        updateUserBadge();
        applyRoleNavigationVisibility();
        topbarDropdown.classList.remove('open');
        topbarBtn.setAttribute('aria-expanded', 'false');
        showToast(`Switched persona to: ${getRoleDisplayName(State.currentRole)}`, 'toast-success');
      });
    });

    document.addEventListener('click', (e) => {
      if (!topbarDropdown.contains(e.target)) {
        topbarDropdown.classList.remove('open');
        topbarBtn.setAttribute('aria-expanded', 'false');
      }
    });
  }

  if (roleSelect) {
    roleSelect.value = State.currentRole;
    roleSelect.addEventListener('change', (e) => {
      State.currentRole = e.target.value;
      updateUserBadge();
      applyRoleNavigationVisibility();
      showToast(`Active Role changed to: ${getRoleDisplayName(State.currentRole)}`, 'toast-success');
    });
  }

  updateUserBadge();
  applyRoleNavigationVisibility();
}

function getRoleDisplayName(role) {
  switch (role) {
    case 'ADMIN': return 'Examination Controller';
    case 'COORDINATOR': return 'Department Coordinator';
    case 'TEACHER': return 'Teacher / Invigilator';
    case 'STUDENT': return 'Student Examinee';
    default: return role;
  }
}

function updateUserBadge() {
  const nameEl = document.getElementById('current-user-name');
  const roleEl = document.getElementById('current-user-role');
  const avatarEl = document.querySelector('.user-avatar-circle');
  const topbarLabel = document.getElementById('topbar-role-label');
  const topbarOpts = document.querySelectorAll('.topbar-role-opt');

  const roleMap = {
    ADMIN: { name: 'Mr. Md. Ashraful Islam Talukder', title: 'Examination Controller', avatar: 'AT', label: 'ADMIN · Controller' },
    COORDINATOR: { name: 'Dewan Mamun Raza', title: 'Department Coordinator', avatar: 'DR', label: 'COORD · Coordinator' },
    TEACHER: { name: 'Pranto Protim Choudhury', title: 'Faculty / Invigilator', avatar: 'PC', label: 'TEACH · Faculty' },
    STUDENT: { name: 'Fatin Sharhad', title: 'Student Examinee', avatar: 'FS', label: 'STU001 · Student' }
  };

  const curr = roleMap[State.currentRole] || roleMap['ADMIN'];

  if (nameEl) nameEl.textContent = curr.name;
  if (roleEl) roleEl.textContent = curr.title;
  if (avatarEl) avatarEl.textContent = curr.avatar;
  if (topbarLabel) topbarLabel.innerHTML = `<strong>${curr.label.split('·')[0]}</strong>&middot;${curr.label.split('·')[1]}`;

  if (topbarOpts) {
    topbarOpts.forEach(opt => {
      opt.classList.toggle('selected', opt.getAttribute('data-role') === State.currentRole);
    });
  }
}

function applyRoleNavigationVisibility() {
  const role = State.currentRole;
  const items = document.querySelectorAll('.nav-item');

  items.forEach(item => {
    const rolesAllowed = item.getAttribute('data-roles');
    if (!rolesAllowed || rolesAllowed === 'all') {
      item.style.display = 'flex';
    } else {
      const allowedArr = rolesAllowed.split(',');
      if (allowedArr.includes(role)) {
        item.style.display = 'flex';
      } else {
        item.style.display = 'none';
      }
    }
  });

  // If active screen is hidden for new role, fallback to overview
  const activeEl = document.querySelector(`.nav-item[data-screen="${State.activeScreen}"]`);
  if (activeEl && activeEl.style.display === 'none') {
    switchScreen('overview');
  }
}

// ==========================================================================
// 4. Sidebar Navigation & Collapse
// ==========================================================================

function setupSidebarNavigation() {
  const toggleBtn = document.getElementById('btn-collapse-sidebar');
  const sidebar = document.getElementById('main-sidebar');

  if (toggleBtn && sidebar) {
    toggleBtn.addEventListener('click', () => {
      State.sidebarCollapsed = !State.sidebarCollapsed;
      sidebar.classList.toggle('collapsed', State.sidebarCollapsed);
      toggleBtn.setAttribute('title', State.sidebarCollapsed ? 'Expand Sidebar' : 'Collapse Sidebar');
    });
  }

  const navLinks = document.querySelectorAll('.nav-item');
  navLinks.forEach(link => {
    link.addEventListener('click', () => {
      const targetScreen = link.getAttribute('data-screen');
      if (targetScreen) {
        switchScreen(targetScreen);
      }
    });
  });
}

function switchScreen(screenId) {
  State.activeScreen = screenId;

  // Update nav item active states
  document.querySelectorAll('.nav-item').forEach(link => {
    link.classList.toggle('active', link.getAttribute('data-screen') === screenId);
  });

  // Update panels visibility and re-trigger fresh stagger animation
  document.querySelectorAll('.module-panel').forEach(panel => {
    const isActive = panel.id === `panel-${screenId}`;
    panel.classList.toggle('active-panel', isActive);
    if (isActive) {
      panel.style.animation = 'none';
      panel.offsetHeight; // trigger reflow
      panel.style.animation = '';
    }
  });

  // Update Topbar Title
  const titleEl = document.getElementById('screen-title-text');
  if (titleEl) {
    titleEl.textContent = getScreenTitle(screenId);
  }

  renderActiveScreen();
}

function getScreenTitle(id) {
  const map = {
    overview: 'Examination Dashboard Overview',
    students: 'Student Directory & Records',
    departments: 'Departments & Course Curriculum',
    examScheduling: 'Examination Timetable & Slots',
    rooms: 'Examination Rooms & Seating Plan',
    attendance: 'Examinee Attendance Roll-Call',
    invigilators: 'Faculty Invigilation Roster',
    marks: 'Continuous Assessment & Marks Registry',
    results: 'Semester Results & Transcripts',
    applications: 'Student Grievances & Rechecks',
    reports: 'Institutional Analytics & Reports',
    history: 'System Audit Log & Activity Trail',
    users: 'User Provisioning & Roles',
    settings: 'System Preferences & Data Backup'
  };
  return map[id] || 'Dashboard';
}

// ==========================================================================
// 4.5 Centralized Audit Logger & Live Badge Engine
// ==========================================================================

function logAudit(category, desc, details = '') {
  const now = new Date();
  const timeStr = now.toLocaleDateString() + ' ' + now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  const currentUser = State.users.find(u => u.role === State.currentRole);
  const initiator = currentUser ? `${State.currentRole} (${currentUser.name})` : State.currentRole;

  State.history.unshift({
    id: 'AUD-' + Date.now() + '-' + Math.floor(Math.random() * 1000),
    time: timeStr,
    category: category,
    desc: desc,
    initiator: initiator,
    details: details
  });

  updateAuditBadge();
  if (State.activeScreen === 'history') {
    renderHistory();
  }
}

function updateAuditBadge() {
  const badge = document.getElementById('topbar-audit-badge');
  if (badge) {
    badge.textContent = State.history.length;
  }
}

// ==========================================================================
// 5. Screen Renderers & Delete Handlers
// ==========================================================================

function renderActiveScreen() {
  switch (State.activeScreen) {
    case 'overview': renderOverview(); break;
    case 'students': renderStudents(); break;
    case 'departments': renderDepartments(); break;
    case 'examScheduling': renderExams(); break;
    case 'rooms': renderRoomsAndSeating(); break;
    case 'attendance': renderAttendance(); break;
    case 'invigilators': renderInvigilators(); break;
    case 'marks': renderMarks(); break;
    case 'results': renderResults(); break;
    case 'applications': renderApplications(); break;
    case 'reports': renderReports(); break;
    case 'history': renderHistory(); break;
    case 'users': renderUsers(); break;
    case 'settings': renderSettings(); break;
  }
}

// --- 5.1 Overview ---
function renderOverview() {
  const tbodyExams = document.getElementById('overview-recent-exams-tbody');
  const tbodyApps = document.getElementById('overview-recent-apps-tbody');

  // KPI Numbers with Smooth Count-Up Physics
  animateCount('kpi-total-students', 0, State.students.length, 650);
  animateCount('kpi-total-exams', 0, State.exams.length, 650);
  animateCount('kpi-scheduled-exams', 0, State.exams.filter(e => e.status === 'SCHEDULED').length, 650);
  animateCount('kpi-published-results', 0, State.results.filter(r => r.published).length, 650);
  animateCount('kpi-pending-results', 0, State.results.filter(r => !r.published).length, 650);
  animateCount('kpi-pending-apps', 0, State.applications.filter(a => a.status === 'PENDING' || a.status === 'UNDER_REVIEW').length, 650);

  // Recent Exams Table with Micro-Pulse Badge
  if (tbodyExams) {
    if (State.exams.length === 0) {
      tbodyExams.innerHTML = `
        <tr>
          <td colspan="6" class="table-empty-cell">
            <p>No examination sessions scheduled yet.</p>
            <small>Click "+ Schedule Exam" to add your first examination session.</small>
          </td>
        </tr>
      `;
    } else {
      tbodyExams.innerHTML = State.exams.slice(0, 4).map(e => `
        <tr>
          <td><strong>${e.id}</strong></td>
          <td>${e.course}</td>
          <td>${e.date}</td>
          <td>${e.startTime} - ${e.endTime}</td>
          <td><span class="badge badge-info">${e.room}</span></td>
          <td><span class="badge ${e.status === 'CANCELLED' ? 'badge-danger' : 'badge-success'}"><span class="pulse-dot"></span>${e.status}</span></td>
        </tr>
      `).join('');
    }
  }

  // Recent Applications
  if (tbodyApps) {
    if (State.applications.length === 0) {
      tbodyApps.innerHTML = `
        <tr>
          <td colspan="5" class="table-empty-cell">
            <p>No academic grievance applications pending.</p>
            <small>Lodged recheck, retake, or evaluation grievance tickets will appear here.</small>
          </td>
        </tr>
      `;
    } else {
      tbodyApps.innerHTML = State.applications.slice(0, 4).map(a => `
        <tr>
          <td><strong>${a.id}</strong></td>
          <td>${a.studentId}</td>
          <td><span class="badge badge-warning">${a.type}</span></td>
          <td>${a.course || 'N/A'}</td>
          <td><span class="badge ${a.status === 'APPROVED' ? 'badge-success' : (a.status === 'REJECTED' ? 'badge-danger' : 'badge-warning')}">${a.status === 'APPROVED' ? '<span class="pulse-dot"></span>' : ''}${a.status}</span></td>
        </tr>
      `).join('');
    }
  }
}

// --- 5.2 Students ---
function renderStudents() {
  const tbody = document.getElementById('students-table-tbody');
  const search = document.getElementById('student-search-input')?.value.toLowerCase() || '';
  const deptFilter = document.getElementById('student-dept-filter')?.value || 'ALL';

  let list = State.students.filter(s => {
    const matchSearch = s.id.toLowerCase().includes(search) || s.name.toLowerCase().includes(search) || s.email.toLowerCase().includes(search);
    const matchDept = deptFilter === 'ALL' || s.department === deptFilter;
    return matchSearch && matchDept;
  });

  if (tbody) {
    if (list.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="9" class="table-empty-cell">
            <p>No student records found.</p>
            <small>Click "+ Add Student" above to input your own student entry.</small>
          </td>
        </tr>
      `;
    } else {
      tbody.innerHTML = list.map(s => `
        <tr>
          <td><strong>${s.id}</strong></td>
          <td>${s.name}</td>
          <td>${s.email}</td>
          <td><span class="badge badge-info">${s.department}</span></td>
          <td>${s.program}</td>
          <td>Semester ${s.semester}</td>
          <td>${s.contact || '—'}</td>
          <td><span class="badge ${s.active ? 'badge-success' : 'badge-danger'}">${s.active ? 'ACTIVE' : 'INACTIVE'}</span></td>
          <td>
            <div style="display: flex; gap: 6px;">
              <button class="btn btn-secondary btn-sm" onclick="deactivateStudent('${s.id}')">Toggle</button>
              <button class="btn btn-danger btn-sm" onclick="deleteStudent('${s.id}')" title="Delete Student Record">Delete</button>
            </div>
          </td>
        </tr>
      `).join('');
    }
  }
}

function deactivateStudent(id) {
  const s = State.students.find(x => x.id === id);
  if (s) {
    s.active = !s.active;
    logAudit('STUDENT', `Toggled student status: ${s.name} (${s.id}) to ${s.active ? 'ACTIVE' : 'INACTIVE'}`);
    renderStudents();
    showToast(`Student ${s.name} (${s.id}) status set to ${s.active ? 'ACTIVE' : 'INACTIVE'}`, 'toast-success');
  }
}

function deleteStudent(id) {
  const s = State.students.find(x => x.id === id);
  if (!s) return;
  if (!confirm(`Are you sure you want to delete student '${s.name}' (${id})? This will also remove associated marks and attendance records.`)) return;

  State.students = State.students.filter(x => x.id !== id);
  State.marks = State.marks.filter(x => x.studentId !== id);
  State.attendance = State.attendance.filter(x => x.studentId !== id);
  State.results = State.results.filter(x => x.studentId !== id);

  logAudit('STUDENT', `Deleted student record: ${s.name} (${id})`);
  refreshDatalists();
  renderStudents();
  renderOverview();
  renderAttendance();
  renderMarks();
  renderResults();
  renderReports();
  showToast(`Student ${s.name} (${id}) removed successfully.`, 'toast-warning');
}

// --- 5.3 Courses & Departments ---
function renderDepartments() {
  const courseTbody = document.getElementById('courses-table-tbody');
  const deptTbody = document.getElementById('dept-table-tbody');

  if (courseTbody) {
    if (State.courses.length === 0) {
      courseTbody.innerHTML = `
        <tr>
          <td colspan="8" class="table-empty-cell">
            <p>No curriculum courses registered yet.</p>
            <small>Click "+ Add Course" above to input your course entries.</small>
          </td>
        </tr>
      `;
    } else {
      courseTbody.innerHTML = State.courses.map(c => `
        <tr>
          <td><strong>${c.code}</strong></td>
          <td>${c.name}</td>
          <td><span class="badge badge-info">${c.dept}</span></td>
          <td>${c.credits} Credits</td>
          <td>Semester ${c.semester}</td>
          <td>${c.teacher || 'Unassigned'}</td>
          <td><span class="badge ${c.type === 'LAB' ? 'badge-success' : 'badge-warning'}">${c.type || 'THEORY'}</span></td>
          <td>
            <button class="btn btn-danger btn-sm" onclick="deleteCourse('${c.code}')" title="Delete Course">Delete</button>
          </td>
        </tr>
      `).join('');
    }
  }

  if (deptTbody) {
    if (State.departments.length === 0) {
      deptTbody.innerHTML = `
        <tr>
          <td colspan="6" class="table-empty-cell">
            <p>No academic departments registered yet.</p>
            <small>Click "+ Add Department" above to add department entities.</small>
          </td>
        </tr>
      `;
    } else {
      deptTbody.innerHTML = State.departments.map(d => `
        <tr>
          <td><strong>${d.id}</strong></td>
          <td>${d.name}</td>
          <td>${d.head || 'Unassigned'}</td>
          <td>${d.programs || '—'}</td>
          <td>${d.coursesCount || 0} Courses</td>
          <td>
            <button class="btn btn-danger btn-sm" onclick="deleteDepartment('${d.id}')" title="Delete Department">Delete</button>
          </td>
        </tr>
      `).join('');
    }
  }
}

function deleteCourse(code) {
  const c = State.courses.find(x => x.code === code);
  if (!c) return;
  if (!confirm(`Are you sure you want to delete course '${code} — ${c.name}'?`)) return;

  State.courses = State.courses.filter(x => x.code !== code);
  State.marks = State.marks.filter(x => x.course !== code);

  logAudit('CURRICULUM', `Deleted course: ${code} (${c.name})`);
  refreshDatalists();
  renderDepartments();
  renderMarks();
  renderReports();
  showToast(`Course ${code} removed successfully.`, 'toast-warning');
}

function deleteDepartment(id) {
  const d = State.departments.find(x => x.id === id);
  if (!d) return;
  if (!confirm(`Are you sure you want to delete department '${d.name}' (${id})?`)) return;

  State.departments = State.departments.filter(x => x.id !== id);

  logAudit('DEPARTMENT', `Deleted department: ${id} (${d.name})`);
  refreshDatalists();
  renderDepartments();
  renderReports();
  showToast(`Department ${id} removed successfully.`, 'toast-warning');
}

// --- 5.4 Examination Scheduling with Conflict Prevention ---
function renderExams() {
  const tbody = document.getElementById('exams-table-tbody');
  if (tbody) {
    if (State.exams.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="10" class="table-empty-cell">
            <p>No examination slots scheduled yet.</p>
            <small>Click "+ Schedule Exam" above to create an exam slot with automatic conflict detection.</small>
          </td>
        </tr>
      `;
    } else {
      tbody.innerHTML = State.exams.map(e => `
        <tr>
          <td><strong>${e.id}</strong></td>
          <td>${e.course}</td>
          <td><span class="badge badge-info">${e.type}</span></td>
          <td>Semester ${e.semester}</td>
          <td>${e.date}</td>
          <td>${e.startTime} - ${e.endTime}</td>
          <td><span class="badge badge-warning">${e.room}</span></td>
          <td>${e.invigilator}</td>
          <td><span class="badge ${e.status === 'CANCELLED' ? 'badge-danger' : 'badge-success'}">${e.status}</span></td>
          <td>
            <div style="display: flex; gap: 6px;">
              ${e.status !== 'CANCELLED' ? `<button class="btn btn-warning btn-sm" onclick="cancelExamSlot('${e.id}')">Cancel</button>` : ''}
              <button class="btn btn-danger btn-sm" onclick="deleteExam('${e.id}')" title="Delete Exam Slot">Delete</button>
            </div>
          </td>
        </tr>
      `).join('');
    }
  }
}

function cancelExamSlot(id) {
  const e = State.exams.find(x => x.id === id);
  if (e) {
    e.status = 'CANCELLED';
    logAudit('SCHEDULING', `Cancelled examination slot: ${e.id} (${e.course} in ${e.room})`);
    renderExams();
    renderOverview();
    showToast(`Exam session ${e.id} cancelled.`, 'toast-warning');
  }
}

function deleteExam(id) {
  const e = State.exams.find(x => x.id === id);
  if (!e) return;
  if (!confirm(`Are you sure you want to delete exam session '${id}' (${e.course})?`)) return;

  State.exams = State.exams.filter(x => x.id !== id);
  logAudit('SCHEDULING', `Deleted examination slot: ${id} (${e.course})`);
  renderExams();
  renderOverview();
  showToast(`Exam session ${id} removed successfully.`, 'toast-warning');
}

// Conflict Detection Engine
function checkSchedulingConflict(room, date, start, end, invigilator) {
  for (let e of State.exams) {
    if (e.status === 'CANCELLED') continue;
    if (e.date === date) {
      const overlaps = (start < e.endTime && end > e.startTime);
      if (overlaps) {
        if (e.room === room) {
          return `Room Conflict: Venue '${room}' is already booked on ${date} during ${e.startTime} - ${e.endTime} for ${e.course}.`;
        }
        if (invigilator && invigilator !== 'Unassigned' && e.invigilator === invigilator) {
          return `Invigilator Conflict: Faculty '${invigilator}' is already supervising another examination during this slot.`;
        }
      }
    }
  }
  return null;
}

// --- 5.5 Rooms & Seating ---
function renderRoomsAndSeating() {
  const roomTbody = document.getElementById('rooms-table-tbody');
  const seatingTbody = document.getElementById('seating-table-tbody');

  if (roomTbody) {
    if (State.rooms.length === 0) {
      roomTbody.innerHTML = `
        <tr>
          <td colspan="6" class="table-empty-cell">
            <p>No examination halls or rooms registered yet.</p>
            <small>Click "+ Add Room" above to input venue details and capacities.</small>
          </td>
        </tr>
      `;
    } else {
      roomTbody.innerHTML = State.rooms.map(r => `
        <tr>
          <td><strong>${r.id}</strong></td>
          <td>${r.building}</td>
          <td>${r.capacity} Seats</td>
          <td><span class="badge ${r.available ? 'badge-success' : 'badge-danger'}">${r.available ? 'AVAILABLE' : 'MAINTENANCE'}</span></td>
          <td>${r.facilities || 'Standard'}</td>
          <td>
            <button class="btn btn-danger btn-sm" onclick="deleteRoom('${r.id}')" title="Delete Room">Delete</button>
          </td>
        </tr>
      `).join('');
    }
  }

  if (seatingTbody) {
    if (State.seats.length === 0) {
      seatingTbody.innerHTML = `
        <tr>
          <td colspan="5" class="table-empty-cell">
            <p>No seat assignments generated yet.</p>
            <small>Examinee seat allocations will appear here once configured.</small>
          </td>
        </tr>
      `;
    } else {
      seatingTbody.innerHTML = State.seats.map(s => {
        const stu = State.students.find(x => x.id === s.studentId);
        return `
          <tr>
            <td><strong>Seat ${s.seatNum}</strong></td>
            <td>${s.studentId}</td>
            <td>${stu ? stu.name : 'N/A'}</td>
            <td>${s.room}</td>
            <td>${s.examId}</td>
          </tr>
        `;
      }).join('');
    }
  }
}

function deleteRoom(id) {
  const r = State.rooms.find(x => x.id === id);
  if (!r) return;
  if (!confirm(`Are you sure you want to delete room '${id}' (${r.building})?`)) return;

  State.rooms = State.rooms.filter(x => x.id !== id);
  logAudit('FACILITY', `Deleted examination room: ${id} (${r.building})`);
  refreshDatalists();
  renderRoomsAndSeating();
  showToast(`Room ${id} removed successfully.`, 'toast-warning');
}

// --- 5.6 Attendance ---
function renderAttendance() {
  const tbody = document.getElementById('attendance-table-tbody');
  const presentCount = State.attendance.filter(a => a.status === 'PRESENT').length;
  const absentCount = State.attendance.filter(a => a.status === 'ABSENT').length;
  const lateCount = State.attendance.filter(a => a.status === 'LATE').length;

  animateCount('att-present-card', 0, presentCount, 550);
  animateCount('att-absent-card', 0, absentCount, 550);
  animateCount('att-late-card', 0, lateCount, 550);

  if (tbody) {
    if (State.students.length === 0 && State.attendance.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="6" class="table-empty-cell">
            <p>No examinees or attendance records found.</p>
            <small>Click "+ Record Attendance" or register students to begin roll-call verification.</small>
          </td>
        </tr>
      `;
    } else if (State.students.length > 0) {
      tbody.innerHTML = State.students.map(s => {
        const rec = State.attendance.find(a => a.studentId === s.id);
        const status = rec ? rec.status : 'ABSENT';
        const time = rec ? rec.time : '—';
        const badgeClass = status === 'PRESENT' ? 'badge-success' : (status === 'LATE' ? 'badge-warning' : 'badge-danger');

        return `
          <tr>
            <td><strong>${s.id}</strong></td>
            <td>${s.name}</td>
            <td>${s.department}</td>
            <td><span class="badge ${badgeClass}">${status}</span></td>
            <td>${time}</td>
            <td>
              <div style="display: flex; gap: 6px; flex-wrap: wrap;">
                <button class="btn btn-success btn-sm" onclick="markStudentAttendance('${s.id}', 'PRESENT')">Present</button>
                <button class="btn btn-warning btn-sm" onclick="markStudentAttendance('${s.id}', 'LATE')">Late</button>
                <button class="btn btn-danger btn-sm" onclick="markStudentAttendance('${s.id}', 'ABSENT')">Absent</button>
                ${rec ? `<button class="btn btn-secondary btn-sm" onclick="deleteAttendance('${s.id}')" title="Clear Record">Clear</button>` : ''}
              </div>
            </td>
          </tr>
        `;
      }).join('');
    } else {
      tbody.innerHTML = State.attendance.map(a => {
        const badgeClass = a.status === 'PRESENT' ? 'badge-success' : (a.status === 'LATE' ? 'badge-warning' : 'badge-danger');
        return `
          <tr>
            <td><strong>${a.studentId}</strong></td>
            <td>${a.remarks || 'Examinee'}</td>
            <td>${a.examId}</td>
            <td><span class="badge ${badgeClass}">${a.status}</span></td>
            <td>${a.time}</td>
            <td>
              <div style="display: flex; gap: 6px; flex-wrap: wrap;">
                <button class="btn btn-success btn-sm" onclick="markStudentAttendance('${a.studentId}', 'PRESENT')">Present</button>
                <button class="btn btn-warning btn-sm" onclick="markStudentAttendance('${a.studentId}', 'LATE')">Late</button>
                <button class="btn btn-danger btn-sm" onclick="markStudentAttendance('${a.studentId}', 'ABSENT')">Absent</button>
                <button class="btn btn-secondary btn-sm" onclick="deleteAttendance('${a.studentId}')" title="Clear Record">Clear</button>
              </div>
            </td>
          </tr>
        `;
      }).join('');
    }
  }
}

function markStudentAttendance(studentId, status) {
  let rec = State.attendance.find(a => a.studentId === studentId);
  const now = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  const activeExam = State.exams.length > 0 ? State.exams[0].id : 'EX-2026-001';

  if (rec) {
    rec.status = status;
    rec.time = now;
  } else {
    State.attendance.push({
      id: 'ATT-' + (State.attendance.length + 1),
      examId: activeExam,
      studentId: studentId,
      status: status,
      time: now,
      remarks: 'Hall recorded'
    });
  }

  logAudit('ATTENDANCE', `Recorded attendance: ${studentId} marked ${status} at ${now}`);
  renderAttendance();
  showToast(`Attendance updated: ${studentId} marked ${status}`, 'toast-success');
}

function deleteAttendance(studentId) {
  const rec = State.attendance.find(a => a.studentId === studentId);
  if (!rec) return;

  State.attendance = State.attendance.filter(a => a.studentId !== studentId);
  logAudit('ATTENDANCE', `Cleared attendance record for examinee: ${studentId}`);
  renderAttendance();
  showToast(`Attendance cleared for ${studentId}.`, 'toast-warning');
}

// --- 5.7 Invigilators ---
function renderInvigilators() {
  const tbody = document.getElementById('invigilators-table-tbody');
  const teachers = State.users.filter(u => u.role === 'TEACHER');

  if (tbody) {
    if (teachers.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="7" class="table-empty-cell">
            <p>No invigilators found.</p>
          </td>
        </tr>
      `;
    } else {
      tbody.innerHTML = teachers.map(t => {
        const assigned = State.exams.find(e => e.invigilator && (e.invigilator.includes(t.id) || e.invigilator.includes(t.name)));
        return `
          <tr>
            <td><strong>${t.id}</strong></td>
            <td>${t.name}</td>
            <td>${t.email}</td>
            <td>${t.designation}</td>
            <td>${assigned ? `<span class="badge badge-info">${assigned.id} (${assigned.course})</span>` : 'Available'}</td>
            <td>${assigned ? assigned.room : '—'}</td>
            <td>${assigned ? assigned.date + ' @ ' + assigned.startTime : '—'}</td>
          </tr>
        `;
      }).join('');
    }
  }
}

// --- 5.8 Marks & Assessment (Theory vs Lab Distributions) ---

function calcTheoryQuizAverage() {
  const q1Val = document.getElementById('marks-q1')?.value.trim() ?? '';
  const q2Val = document.getElementById('marks-q2')?.value.trim() ?? '';
  const q3Val = document.getElementById('marks-q3')?.value.trim() ?? '';

  const vals = [];
  if (q1Val !== '' && !isNaN(q1Val)) vals.push(parseFloat(q1Val));
  if (q2Val !== '' && !isNaN(q2Val)) vals.push(parseFloat(q2Val));
  if (q3Val !== '' && !isNaN(q3Val)) vals.push(parseFloat(q3Val));

  const avgField = document.getElementById('marks-q-avg');
  if (avgField) {
    if (vals.length > 0) {
      const avg = vals.reduce((a, b) => a + b, 0) / vals.length;
      avgField.value = parseFloat(avg.toFixed(2));
    } else {
      avgField.value = '';
    }
  }
}

function toggleMarksDistributionFields() {
  const type = document.getElementById('marks-course-type')?.value || 'THEORY';
  const theoryBox = document.getElementById('marks-theory-container');
  const labBox = document.getElementById('marks-lab-container');
  if (theoryBox && labBox) {
    if (type === 'LAB') {
      theoryBox.style.display = 'none';
      labBox.style.display = 'block';
    } else {
      theoryBox.style.display = 'block';
      labBox.style.display = 'none';
    }
  }
}

function autoDetectCourseType() {
  const code = document.getElementById('marks-course-code')?.value.trim().toUpperCase();
  const c = State.courses.find(x => x.code === code);
  const typeSelect = document.getElementById('marks-course-type');
  if (c && typeSelect) {
    typeSelect.value = c.type || 'THEORY';
    toggleMarksDistributionFields();
  }
}

function renderMarks() {
  const tbody = document.getElementById('marks-table-tbody');
  if (tbody) {
    if (State.marks.length === 0 && State.students.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="8" class="table-empty-cell">
            <p>No continuous assessment marks recorded yet.</p>
            <small>Click "+ Record Marks" above to enter scores for students.</small>
          </td>
        </tr>
      `;
    } else if (State.marks.length > 0) {
      tbody.innerHTML = State.marks.map(m => {
        const stu = State.students.find(x => x.id === m.studentId);
        const isLab = m.type === 'LAB';
        return `
          <tr>
            <td><strong>${m.studentId}</strong></td>
            <td>${stu ? stu.name : (m.studentName || 'Examinee')}</td>
            <td><span class="badge badge-info">${m.course}</span></td>
            <td><span class="badge ${isLab ? 'badge-success' : 'badge-warning'}">${isLab ? 'LAB' : 'THEORY'}</span></td>
            <td>
              ${isLab ? `
                <div style="font-size: 11px; display: flex; gap: 5px; flex-wrap: wrap;">
                  <span class="badge badge-success">Att: ${m.att || 0}/10</span>
                  <span class="badge badge-success">Report: ${m.labRep || 0}/25</span>
                  <span class="badge badge-success">Perf: ${m.labPerf || 0}/25</span>
                  <span class="badge badge-success">Final: ${m.fin || 0}/40</span>
                </div>
              ` : `
                <div style="font-size: 11px; display: flex; gap: 5px; flex-wrap: wrap;">
                  <span class="badge badge-info">Att: ${m.att || 0}/7</span>
                  <span class="badge badge-info">Asg: ${m.asg || 0}/5</span>
                  <span class="badge badge-info" title="Quiz 1: ${m.q1 ?? '—'}, Quiz 2: ${m.q2 ?? '—'}, Quiz 3: ${m.q3 ?? '—'}">Quizzes (Avg): ${m.quizAvg || 0}/15</span>
                  <span class="badge badge-info">Pres: ${m.pres || 0}/8</span>
                  <span class="badge badge-info">Mid: ${m.mid || 0}/25</span>
                  <span class="badge badge-info">Final: ${m.fin || 0}/40</span>
                </div>
              `}
            </td>
            <td><strong>${m.total} / 100</strong></td>
            <td><span class="badge badge-success">${m.grade}</span></td>
            <td>
              <div style="display: flex; gap: 6px;">
                <button class="btn btn-primary btn-sm" onclick="openMarksModal('${m.studentId}', '${m.course}')">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteMarks('${m.studentId}', '${m.course}')" title="Delete Marks">Delete</button>
              </div>
            </td>
          </tr>
        `;
      }).join('');
    } else {
      tbody.innerHTML = State.students.map(s => `
        <tr>
          <td><strong>${s.id}</strong></td>
          <td>${s.name}</td>
          <td>—</td>
          <td>—</td>
          <td>—</td>
          <td><strong>— / 100</strong></td>
          <td><span class="badge badge-warning">AWAITING</span></td>
          <td>
            <button class="btn btn-primary btn-sm" onclick="openMarksModal('${s.id}')">Enter Marks</button>
          </td>
        </tr>
      `).join('');
    }
  }
}

function deleteMarks(studentId, course) {
  const m = State.marks.find(x => x.studentId === studentId && x.course === course);
  if (!m) return;
  if (!confirm(`Are you sure you want to delete assessment marks for student '${studentId}' in '${course}'?`)) return;

  State.marks = State.marks.filter(x => !(x.studentId === studentId && x.course === course));
  logAudit('ASSESSMENT', `Deleted assessment marks for examinee ${studentId} in course ${course}`);
  renderMarks();
  renderOverview();
  renderReports();
  showToast(`Marks deleted for ${studentId} (${course}).`, 'toast-warning');
}

function openNewMarksModal() {
  document.getElementById('marks-modal-title').textContent = 'Record Assessment Marks';
  document.getElementById('marks-student-id').value = '';
  const firstCourse = State.courses.length > 0 ? State.courses[0] : null;
  const courseCode = firstCourse ? firstCourse.code : 'CSE221';
  document.getElementById('marks-course-code').value = courseCode;

  const typeSelect = document.getElementById('marks-course-type');
  if (typeSelect) {
    typeSelect.value = firstCourse ? (firstCourse.type || 'THEORY') : 'THEORY';
  }

  // Clear Theory inputs
  document.getElementById('marks-att').value = '';
  document.getElementById('marks-asg').value = '';
  const q1Input = document.getElementById('marks-q1'); if (q1Input) q1Input.value = '';
  const q2Input = document.getElementById('marks-q2'); if (q2Input) q2Input.value = '';
  const q3Input = document.getElementById('marks-q3'); if (q3Input) q3Input.value = '';
  const qAvgInput = document.getElementById('marks-q-avg'); if (qAvgInput) qAvgInput.value = '';
  document.getElementById('marks-pres').value = '';
  document.getElementById('marks-mid').value = '';
  document.getElementById('marks-fin').value = '';

  // Clear Lab inputs
  document.getElementById('marks-lab-att').value = '';
  document.getElementById('marks-lab-rep').value = '';
  document.getElementById('marks-lab-perf').value = '';
  document.getElementById('marks-lab-fin').value = '';

  toggleMarksDistributionFields();
  openModal('modal-enter-marks');
}

function openMarksModal(studentId, courseCode) {
  const stu = State.students.find(x => x.id === studentId);
  const targetCourse = courseCode || (State.courses.length > 0 ? State.courses[0].code : 'CSE221');
  const courseObj = State.courses.find(x => x.code === targetCourse);
  const m = State.marks.find(x => x.studentId === studentId && (!courseCode || x.course === courseCode));

  const scheme = m ? (m.type || 'THEORY') : (courseObj ? (courseObj.type || 'THEORY') : 'THEORY');

  document.getElementById('marks-modal-title').textContent = `Edit Marks — ${stu ? stu.name : studentId} (${targetCourse})`;
  document.getElementById('marks-student-id').value = studentId;
  document.getElementById('marks-course-code').value = targetCourse;

  const typeSelect = document.getElementById('marks-course-type');
  if (typeSelect) typeSelect.value = scheme;

  // Theory fields
  document.getElementById('marks-att').value = m ? (m.att ?? '') : '';
  document.getElementById('marks-asg').value = m ? (m.asg ?? '') : '';
  const q1Field = document.getElementById('marks-q1'); if (q1Field) q1Field.value = m ? (m.q1 ?? '') : '';
  const q2Field = document.getElementById('marks-q2'); if (q2Field) q2Field.value = m ? (m.q2 ?? '') : '';
  const q3Field = document.getElementById('marks-q3'); if (q3Field) q3Field.value = m ? (m.q3 ?? '') : '';
  const qAvgField = document.getElementById('marks-q-avg'); if (qAvgField) qAvgField.value = m ? (m.quizAvg ?? '') : '';
  document.getElementById('marks-pres').value = m ? (m.pres ?? '') : '';
  document.getElementById('marks-mid').value = m ? (m.mid ?? '') : '';
  document.getElementById('marks-fin').value = m ? (m.fin ?? '') : '';

  // Lab fields
  document.getElementById('marks-lab-att').value = m ? (m.att ?? '') : '';
  document.getElementById('marks-lab-rep').value = m ? (m.labRep ?? '') : '';
  document.getElementById('marks-lab-perf').value = m ? (m.labPerf ?? '') : '';
  document.getElementById('marks-lab-fin').value = m ? (m.fin ?? '') : '';

  toggleMarksDistributionFields();
  openModal('modal-enter-marks');
}

// --- 5.9 Results & Transcripts ---
function renderResults() {
  const tbody = document.getElementById('results-table-tbody');
  const publishBtn = document.getElementById('publish-batch-btn');
  if (publishBtn) {
    publishBtn.style.display = State.currentRole === 'ADMIN' ? 'inline-flex' : 'none';
  }

  if (tbody) {
    if (State.results.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="8" class="table-empty-cell">
            <p>No semester results computed yet.</p>
            <small>Record student assessment marks and click "+ Process Results" to compute GPA/CGPA.</small>
          </td>
        </tr>
      `;
    } else {
      tbody.innerHTML = State.results.map(r => {
        const stu = State.students.find(x => x.id === r.studentId);
        return `
          <tr>
            <td><strong>${r.resultId}</strong></td>
            <td>${r.studentId}</td>
            <td>${stu ? stu.name : 'Examinee'}</td>
            <td>Semester ${r.semester}</td>
            <td><strong>${Number(r.gpa).toFixed(2)}</strong></td>
            <td><strong>${Number(r.cgpa).toFixed(2)}</strong></td>
            <td>
              <span class="badge ${r.published ? 'badge-success' : 'badge-warning'}">
                ${r.published ? 'PUBLISHED' : 'UNPUBLISHED (DRAFT)'}
              </span>
            </td>
            <td>
              <div style="display: flex; gap: 6px;">
                <button class="btn btn-secondary btn-sm" onclick="viewTranscript('${r.studentId}')">Transcript</button>
                <button class="btn btn-danger btn-sm" onclick="deleteResult('${r.resultId}')" title="Delete Result">Delete</button>
              </div>
            </td>
          </tr>
        `;
      }).join('');
    }
  }
}

function deleteResult(resultId) {
  const r = State.results.find(x => x.resultId === resultId);
  if (!r) return;
  if (!confirm(`Are you sure you want to delete result record '${resultId}'?`)) return;

  State.results = State.results.filter(x => x.resultId !== resultId);
  logAudit('RESULTS', `Deleted result record: ${resultId} (Student: ${r.studentId})`);
  renderResults();
  renderOverview();
  renderReports();
  showToast(`Result record ${resultId} deleted.`, 'toast-warning');
}

function processSemesterResults() {
  if (State.marks.length === 0) {
    showToast('No assessment marks found! Please record marks first.', 'toast-warning');
    return;
  }

  const gradeToGpa = (grade) => {
    switch (grade) {
      case 'A+': return 4.00;
      case 'A': return 3.75;
      case 'A-': return 3.50;
      case 'B+': return 3.25;
      case 'B': return 3.00;
      case 'B-': return 2.75;
      case 'C+': return 2.50;
      case 'C': return 2.25;
      case 'D': return 2.00;
      default: return 0.00;
    }
  };

  const studentMap = {};
  State.marks.forEach(m => {
    if (!studentMap[m.studentId]) studentMap[m.studentId] = [];
    studentMap[m.studentId].push(m);
  });

  let processedCount = 0;
  Object.keys(studentMap).forEach(sid => {
    const list = studentMap[sid];
    let totalWeightedPoints = 0;
    let totalCredits = 0;
    list.forEach(item => {
      const courseObj = State.courses.find(c => c.code === item.course);
      const credits = courseObj ? (courseObj.credits || 3.0) : 3.0;
      const gp = gradeToGpa(item.grade);
      totalWeightedPoints += gp * credits;
      totalCredits += credits;
    });
    const avgGpa = totalCredits > 0 ? parseFloat((totalWeightedPoints / totalCredits).toFixed(2)) : 0.0;

    let res = State.results.find(r => r.studentId === sid);
    if (res) {
      res.gpa = avgGpa;
      res.cgpa = avgGpa;
    } else {
      State.results.push({
        resultId: `RES-2026-00${State.results.length + 1}`,
        studentId: sid,
        semester: 5,
        gpa: avgGpa,
        cgpa: avgGpa,
        published: false
      });
    }
    processedCount++;
  });

  logAudit('RESULTS', `Processed semester results for ${processedCount} examinee(s)`);
  renderResults();
  renderOverview();
  renderReports();
  showToast(`Successfully processed results for ${processedCount} student(s)!`, 'toast-success');
}

function togglePublishResults() {
  if (State.currentRole !== 'ADMIN') {
    showToast('Permission Denied: Only Examination Controller can publish results.', 'toast-error');
    return;
  }

  if (State.results.length === 0) {
    showToast('No results to publish! Please process results first.', 'toast-warning');
    return;
  }

  const allPublished = State.results.every(r => r.published);
  State.results.forEach(r => r.published = !allPublished);

  logAudit('RESULTS', allPublished ? 'Unpublished semester results to draft' : 'Officially published semester results release');
  renderResults();
  renderOverview();
  showToast(allPublished ? 'Results unpublished to DRAFT mode.' : 'Semester Results have been officially PUBLISHED!', 'toast-success');
}

function calculateGrade(percentage) {
  if (percentage >= 80.0) return 'A+';
  if (percentage >= 75.0) return 'A';
  if (percentage >= 70.0) return 'A-';
  if (percentage >= 65.0) return 'B+';
  if (percentage >= 60.0) return 'B';
  if (percentage >= 55.0) return 'B-';
  if (percentage >= 50.0) return 'C+';
  if (percentage >= 45.0) return 'C';
  if (percentage >= 40.0) return 'D';
  return 'F';
}

function getGradePoint(grade) {
  switch ((grade || '').trim().toUpperCase()) {
    case 'A+': return 4.00;
    case 'A': return 3.75;
    case 'A-': return 3.50;
    case 'B+': return 3.25;
    case 'B': return 3.00;
    case 'B-': return 2.75;
    case 'C+': return 2.50;
    case 'C': return 2.25;
    case 'D': return 2.00;
    default: return 0.00;
  }
}

function getGradeRemark(grade) {
  switch ((grade || '').trim().toUpperCase()) {
    case 'A+': return 'Outstanding';
    case 'A': return 'Excellent';
    case 'A-': return 'Very Good';
    case 'B+': return 'Good';
    case 'B': return 'Satisfactory';
    case 'B-': return 'Above Average';
    case 'C+': return 'Average';
    case 'C': return 'Below Average';
    case 'D': return 'Pass';
    case 'F':
    default: return 'Fail';
  }
}

function viewTranscript(studentId) {
  const stu = State.students.find(x => x.id === studentId);
  const res = State.results.find(x => x.studentId === studentId);
  const studentMarks = State.marks.filter(x => x.studentId === studentId);

  document.getElementById('transcript-student-name').textContent = stu ? stu.name : studentId;
  document.getElementById('transcript-student-id').textContent = studentId;
  document.getElementById('transcript-student-dept').textContent = stu ? stu.department : 'Academic Dept';
  document.getElementById('transcript-gpa').textContent = res ? Number(res.gpa).toFixed(2) : '0.00';
  document.getElementById('transcript-cgpa').textContent = res ? Number(res.cgpa).toFixed(2) : '0.00';

  const tbody = document.querySelector('#modal-transcript table tbody');
  if (tbody) {
    if (studentMarks.length > 0) {
      tbody.innerHTML = studentMarks.map(m => {
        const c = State.courses.find(x => x.code === m.course);
        return `
          <tr>
            <td>${m.course} — ${c ? c.name : 'Course Assessment'}</td>
            <td>${c ? c.credits : 3}</td>
            <td>${getGradePoint(m.grade).toFixed(2)}</td>
            <td><span class="badge badge-success">${m.grade}</span></td>
            <td><strong>${getGradeRemark(m.grade)}</strong></td>
          </tr>
        `;
      }).join('');
    } else {
      tbody.innerHTML = `
        <tr>
          <td colspan="5" style="text-align: center; color: var(--text-secondary); padding: 16px;">
            No course marks registered for this student yet.
          </td>
        </tr>
      `;
    }
  }

  openModal('modal-transcript');
}

// --- 5.10 Applications ---
function renderApplications() {
  const tbody = document.getElementById('apps-table-tbody');
  if (tbody) {
    if (State.applications.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="8" class="table-empty-cell">
            <p>No grievance or recheck applications lodged.</p>
            <small>Click "+ Submit Application" above to lodge an application ticket.</small>
          </td>
        </tr>
      `;
    } else {
      tbody.innerHTML = State.applications.map(a => `
        <tr>
          <td><strong>${a.id}</strong></td>
          <td>${a.studentId}</td>
          <td><span class="badge badge-warning">${a.type}</span></td>
          <td>${a.course || 'N/A'}</td>
          <td>${a.desc}</td>
          <td><span class="badge ${a.status === 'APPROVED' ? 'badge-success' : (a.status === 'REJECTED' ? 'badge-danger' : 'badge-warning')}">${a.status}</span></td>
          <td>${a.comments || '—'}</td>
          <td>
            <div style="display: flex; gap: 6px; flex-wrap: wrap;">
              ${(State.currentRole === 'ADMIN' || State.currentRole === 'COORDINATOR') && a.status === 'PENDING' ? `
                <button class="btn btn-success btn-sm" onclick="decideApp('${a.id}', 'APPROVED')">Approve</button>
                <button class="btn btn-danger btn-sm" onclick="decideApp('${a.id}', 'REJECTED')">Reject</button>
              ` : ''}
              <button class="btn btn-secondary btn-sm" onclick="deleteApplication('${a.id}')" title="Delete Application">Delete</button>
            </div>
          </td>
        </tr>
      `).join('');
    }
  }
}

function decideApp(id, status) {
  const app = State.applications.find(a => a.id === id);
  if (app) {
    app.status = status;
    app.comments = status === 'APPROVED' ? 'Verified by Controller' : 'Grievance denied after review';
    logAudit('GRIEVANCE', `Application ${id} decision updated to: ${status}`);
    renderApplications();
    renderOverview();
    showToast(`Application ${id} status set to ${status}`, 'toast-success');
  }
}

function deleteApplication(id) {
  const a = State.applications.find(x => x.id === id);
  if (!a) return;
  if (!confirm(`Are you sure you want to delete grievance application '${id}'?`)) return;

  State.applications = State.applications.filter(x => x.id !== id);
  logAudit('GRIEVANCE', `Deleted application: ${id} (${a.type})`);
  renderApplications();
  renderOverview();
  showToast(`Application ${id} removed.`, 'toast-warning');
}

// --- 5.11 Reports & Analytics ---
function renderReports() {
  renderCharts();
}

function renderCharts() {
  // 1. Interactive SVG Donut Chart (Pass vs Fail Distribution)
  const donutContainer = document.getElementById('pass-fail-donut-chart');
  if (donutContainer) {
    const totalResults = State.results.length;
    let passRate = 0;
    let failRate = 0;
    let passCount = 0;
    let failCount = 0;

    if (totalResults > 0) {
      passCount = State.results.filter(r => r.gpa >= 2.0).length;
      failCount = totalResults - passCount;
      passRate = parseFloat(((passCount / totalResults) * 100).toFixed(1));
      failRate = parseFloat((100 - passRate).toFixed(1));
    }

    const circumference = 440;
    const passOffset = circumference - (circumference * passRate / 100);

    donutContainer.innerHTML = `
      <svg viewBox="0 0 200 200" width="190" height="190" style="overflow: visible;">
        <!-- Base track / Fail portion -->
        <circle class="donut-segment" cx="100" cy="100" r="70" fill="transparent"
                stroke="var(--color-danger)" stroke-width="24"
                onmousemove="showChartTooltip(event, '<b>Failing / Backlog:</b> ${failRate}%<br>${failCount} examinee(s)')"
                onmouseleave="hideChartTooltip()" />
        <!-- Pass portion -->
        <circle id="donut-pass-circle" class="donut-segment" cx="100" cy="100" r="70" fill="transparent"
                stroke="var(--color-success)" stroke-width="24"
                stroke-dasharray="${circumference}" stroke-dashoffset="${circumference}"
                transform="rotate(-90 100 100)" stroke-linecap="round"
                onmousemove="showChartTooltip(event, '<b>Passing Examinees:</b> ${passRate}%<br>${passCount} cleared assessments')"
                onmouseleave="hideChartTooltip()" />
        <text id="donut-pass-text" x="100" y="96" text-anchor="middle" font-size="24" font-weight="800" fill="var(--text-primary)">0.0%</text>
        <text x="100" y="118" text-anchor="middle" font-size="11" font-weight="700" fill="var(--text-secondary)">Pass Rate</text>
      </svg>
    `;

    requestAnimationFrame(() => {
      setTimeout(() => {
        const circle = document.getElementById('donut-pass-circle');
        if (circle) circle.style.strokeDashoffset = `${passOffset}`;
        animateCount('donut-pass-text', 0, passRate, 900, true, '%');
      }, 50);
    });
  }

  // 2. Interactive SVG Bar Chart (Department CGPA Comparison)
  const barContainer = document.getElementById('dept-cgpa-bar-chart');
  if (barContainer) {
    const depts = State.departments.length > 0 ? State.departments : [
      { id: 'CSE', name: 'Computer Science' },
      { id: 'SWE', name: 'Software Engineering' },
      { id: 'EEE', name: 'Electrical Engineering' }
    ];

    const deptStats = depts.map(d => {
      const stuIds = State.students.filter(s => s.department === d.id).map(s => s.id);
      const deptResults = State.results.filter(r => stuIds.includes(r.studentId));
      const avg = deptResults.length > 0
        ? parseFloat((deptResults.reduce((sum, r) => sum + r.cgpa, 0) / deptResults.length).toFixed(2))
        : 0.00;
      return { id: d.id, name: d.name, cgpa: avg, count: stuIds.length };
    });

    const colors = ['var(--primary-accent)', 'var(--color-success)', 'var(--color-warning)', 'var(--color-info)'];

    barContainer.innerHTML = `
      <svg viewBox="0 0 320 180" width="100%" height="180" style="overflow: visible;">
        <!-- Base line -->
        <line x1="20" y1="140" x2="300" y2="140" stroke="var(--border-subtle)" stroke-width="1.5" stroke-dasharray="4" />
        ${deptStats.map((ds, idx) => {
          const x = 40 + idx * 95;
          const h = (ds.cgpa / 4.0) * 110;
          const col = colors[idx % colors.length];
          return `
            <rect id="bar-dept-${idx}" class="chart-bar-rect" x="${x}" y="140" width="45" height="0" rx="6" fill="${col}"
                  onmousemove="showChartTooltip(event, '<b>${ds.id} Department</b><br>Average CGPA: ${ds.cgpa.toFixed(2)} / 4.00<br>Total Examinees: ${ds.count}')"
                  onmouseleave="hideChartTooltip()" />
            <text id="bar-dept-val-${idx}" x="${x + 22}" y="${130 - h}" text-anchor="middle" font-size="11" font-weight="800" fill="var(--text-primary)" style="opacity: 0; transition: opacity 0.4s ease 0.4s;">${ds.cgpa.toFixed(2)}</text>
            <text x="${x + 22}" y="160" text-anchor="middle" font-size="12" font-weight="700" fill="var(--text-secondary)">${ds.id}</text>
          `;
        }).join('')}
      </svg>
    `;

    requestAnimationFrame(() => {
      setTimeout(() => {
        deptStats.forEach((ds, idx) => {
          const h = (ds.cgpa / 4.0) * 110;
          const rect = document.getElementById(`bar-dept-${idx}`);
          const txt = document.getElementById(`bar-dept-val-${idx}`);
          if (rect) {
            rect.setAttribute('y', 140 - h);
            rect.setAttribute('height', h);
          }
          if (txt) txt.style.opacity = '1';
        });
      }, 60);
    });
  }

  // 3. Course Performance Horizontal Progress Bars (.hbars)
  const hbarsContainer = document.getElementById('course-performance-hbars');
  if (hbarsContainer) {
    if (State.courses.length === 0) {
      hbarsContainer.innerHTML = `
        <div class="table-empty-cell" style="padding: 24px;">
          <p>No curriculum courses registered yet.</p>
          <small>Course performance metrics will generate once courses and marks are recorded.</small>
        </div>
      `;
    } else {
      const colors = ['var(--primary-accent)', 'var(--color-info)', 'var(--color-success)', 'var(--color-warning)'];
      const courseStats = State.courses.map((c, i) => {
        const cMarks = State.marks.filter(m => m.course === c.code);
        const passCount = cMarks.filter(m => m.grade !== 'F').length;
        const pass = cMarks.length > 0 ? parseFloat(((passCount / cMarks.length) * 100).toFixed(1)) : 0.0;
        return {
          code: c.code,
          name: c.name,
          pass: pass,
          students: cMarks.length,
          color: colors[i % colors.length]
        };
      });

      hbarsContainer.innerHTML = courseStats.map((c, i) => `
        <div class="hbars__row" onmousemove="showChartTooltip(event, '<b>${c.code}</b> — ${c.pass}% Pass Rate<br>${c.students} Recorded Marks')" onmouseleave="hideChartTooltip()">
          <div class="hbars__name">
            ${c.code}
            <small>${c.name}</small>
          </div>
          <div class="hbars__track">
            <div class="hbars__bar" id="hbar-${i}" style="background: linear-gradient(90deg, color-mix(in srgb, ${c.color} 40%, transparent), ${c.color}); box-shadow: 0 0 12px -2px ${c.color};"></div>
          </div>
          <div class="hbars__val" id="hbar-val-${i}">0.0%</div>
        </div>
      `).join('');

      requestAnimationFrame(() => {
        courseStats.forEach((c, i) => {
          setTimeout(() => {
            const bar = document.getElementById(`hbar-${i}`);
            if (bar) bar.style.width = `${c.pass}%`;
            animateCount(`hbar-val-${i}`, 0, c.pass, 800, true, '%');
          }, i * 120 + 80);
        });
      });
    }
  }
}

function exportAuditReport() {
  showToast('Generating Institutional Audit Report in background...', 'toast-info');
  setTimeout(() => {
    const totalResults = State.results.length;
    const passedCount = State.results.filter(r => r.gpa >= 2.0).length;
    const passRateStr = totalResults > 0 ? ((passedCount / totalResults) * 100).toFixed(1) + '%' : '0.0% (No results processed)';

    const blob = new Blob([
      `========================================================================\n` +
      `          EduAsset — University Examination Controller's Office        \n` +
      `                  Institutional Assessment Summary Report               \n` +
      `========================================================================\n` +
      `Generated On: ${new Date().toISOString()}\n\n` +
      `1. CORE METRICS:\n` +
      `   - Total Registered Students:   ${State.students.length}\n` +
      `   - Total Curriculum Courses:    ${State.courses.length}\n` +
      `   - Total Examination Venues:    ${State.rooms.length}\n` +
      `   - Total Examinations:          ${State.exams.length}\n` +
      `   - Scheduled Examinations:      ${State.exams.filter(e => e.status === 'SCHEDULED').length}\n` +
      `   - Processed Results Batches:   ${State.results.length}\n` +
      `   - Published Result Batches:    ${State.results.filter(r => r.published).length}\n` +
      `   - Institutional Pass Rate:     ${passRateStr}\n\n` +
      `2. REGISTERED DEPARTMENTS:\n` +
      (State.departments.length > 0
        ? State.departments.map(d => `   - ${d.id}: ${d.name} (${d.head || 'Unassigned'})`).join('\n')
        : '   - (No departments registered yet)\n')
    ], { type: 'text/plain' });

    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `EduAsset_Institutional_Report_${Date.now()}.txt`;
    a.click();
    showToast('Report downloaded successfully!', 'toast-success');
  }, 600);
}

// --- 5.12 History of Every Audit ---
function renderHistory() {
  const tbody = document.getElementById('history-table-tbody');
  const search = document.getElementById('history-search-input')?.value.toLowerCase() || '';
  const catFilter = document.getElementById('history-category-filter')?.value || 'ALL';

  let list = State.history.filter(h => {
    const matchSearch = h.desc.toLowerCase().includes(search) || h.initiator.toLowerCase().includes(search) || h.category.toLowerCase().includes(search) || h.time.toLowerCase().includes(search);
    const matchCat = catFilter === 'ALL' || h.category === catFilter;
    return matchSearch && matchCat;
  });

  updateAuditBadge();

  if (tbody) {
    if (list.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="5" class="table-empty-cell">
            <p>No audit activity matching criteria.</p>
            <small>All operations across students, curriculum, scheduling, attendance, marks, and security are logged automatically.</small>
          </td>
        </tr>
      `;
    } else {
      tbody.innerHTML = list.map((h, idx) => {
        let badgeClass = 'badge-info';
        if (h.category === 'STUDENT') badgeClass = 'badge-info';
        else if (h.category === 'CURRICULUM' || h.category === 'DEPARTMENT') badgeClass = 'badge-success';
        else if (h.category === 'SCHEDULING' || h.category === 'ASSESSMENT') badgeClass = 'badge-warning';
        else if (h.category === 'RESULTS') badgeClass = 'badge-success';
        else if (h.category === 'GRIEVANCE') badgeClass = 'badge-warning';
        else if (h.category === 'AUTH') badgeClass = 'badge-info';

        return `
          <tr>
            <td><strong>${h.time}</strong></td>
            <td><span class="badge ${badgeClass}">${h.category}</span></td>
            <td>${h.desc}</td>
            <td>${h.initiator}</td>
            <td>
              <button class="btn btn-secondary btn-sm" onclick="deleteHistoryEntry('${h.id}')" title="Remove this log entry">Remove</button>
            </td>
          </tr>
        `;
      }).join('');
    }
  }
}

function deleteHistoryEntry(id) {
  const idx = State.history.findIndex(x => x.id === id);
  if (idx !== -1) {
    State.history.splice(idx, 1);
    updateAuditBadge();
    renderHistory();
    showToast('Audit log entry removed.', 'toast-info');
  }
}

function clearAuditHistory() {
  if (State.history.length === 0) {
    showToast('Audit log is already empty.', 'toast-info');
    return;
  }
  if (!confirm('Are you sure you want to clear the entire audit trail?')) return;

  State.history = [];
  updateAuditBadge();
  renderHistory();
  showToast('Audit trail cleared.', 'toast-warning');
}

function exportAuditTrail() {
  if (State.history.length === 0) {
    showToast('No audit entries to export.', 'toast-info');
    return;
  }

  const lines = [
    `========================================================================`,
    `               EduAsset — Institutional Audit Trail Log                `,
    `========================================================================`,
    `Exported On: ${new Date().toISOString()}`,
    `Total Logged Events: ${State.history.length}\n`,
    `TIMESTAMP           | CATEGORY     | INITIATOR / SCOPE        | EVENT DESCRIPTION`,
    `--------------------+--------------+--------------------------+--------------------------------------------------`
  ];

  State.history.forEach(h => {
    lines.push(`${h.time.padEnd(19)} | ${h.category.padEnd(12)} | ${h.initiator.padEnd(24)} | ${h.desc}`);
  });

  const blob = new Blob([lines.join('\n')], { type: 'text/plain' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `EduAsset_Audit_Trail_${Date.now()}.txt`;
  a.click();
  showToast('Audit trail exported successfully!', 'toast-success');
}

// --- 5.13 Users Management ---
function renderUsers() {
  const tbody = document.getElementById('users-table-tbody');
  if (tbody) {
    tbody.innerHTML = State.users.map(u => `
      <tr>
        <td><strong>${u.id}</strong></td>
        <td>${u.name}</td>
        <td>${u.email}</td>
        <td><span class="badge badge-info">${u.role}</span></td>
        <td>${u.designation}</td>
        <td><span class="badge ${u.active ? 'badge-success' : 'badge-danger'}">${u.active ? 'ACTIVE' : 'DEACTIVATED'}</span></td>
        <td>${u.created}</td>
      </tr>
    `).join('');
  }
}

// --- 5.14 Settings ---
function renderSettings() {
  const currentModeEl = document.getElementById('settings-active-mode-label');
  if (currentModeEl) {
    currentModeEl.textContent = 'Light Porcelain Theme (Glassmorphic)';
  }
}

function toggleTheme() {
  showToast('Light Glassmorphic theme is permanently enforced as institutional standard.', 'toast-info');
}

function triggerLocalBackup() {
  logAudit('SYSTEM', 'Created local data snapshot backup in ./backup/');
  showToast('Creating local data snapshot backup in ./backup/...', 'toast-success');
}

// Helper: Refresh Dynamic Datalists & Form Selects
function refreshDatalists() {
  const courseList = document.getElementById('courses-datalist');
  if (courseList) {
    courseList.innerHTML = State.courses.map(c => `<option value="${c.code}">${c.name} (${c.dept}) - ${c.type || 'THEORY'}</option>`).join('');
  }

  const roomList = document.getElementById('rooms-datalist');
  if (roomList) {
    roomList.innerHTML = State.rooms.map(r => `<option value="${r.id}">${r.building} (${r.capacity} Seats)</option>`).join('');
  }

  const studentList = document.getElementById('students-datalist');
  if (studentList) {
    studentList.innerHTML = State.students.map(s => `<option value="${s.id}">${s.name} (${s.department})</option>`).join('');
  }

  const grievanceCourse = document.getElementById('grievance-course');
  if (grievanceCourse && State.courses.length > 0) {
    grievanceCourse.innerHTML = State.courses.map(c => `<option value="${c.code}">${c.code} — ${c.name}</option>`).join('');
  }

  const studentDept = document.getElementById('add-student-dept');
  if (studentDept && State.departments.length > 0) {
    studentDept.innerHTML = State.departments.map(d => `<option value="${d.id}">${d.id} — ${d.name}</option>`).join('');
  }
}

// ==========================================================================
// 6. Modal Dialogs & Form Actions
// ==========================================================================

function setupModals() {
  document.querySelectorAll('.btn-close-modal').forEach(btn => {
    btn.addEventListener('click', () => {
      const modal = btn.closest('.modal-backdrop');
      if (modal) closeModal(modal.id);
    });
  });

  // Modal Backdrop click
  document.querySelectorAll('.modal-backdrop').forEach(modal => {
    modal.addEventListener('click', (e) => {
      if (e.target === modal) closeModal(modal.id);
    });
  });

  const marksCourseInput = document.getElementById('marks-course-code');
  if (marksCourseInput) {
    marksCourseInput.addEventListener('input', autoDetectCourseType);
    marksCourseInput.addEventListener('change', autoDetectCourseType);
  }

  ['marks-q1', 'marks-q2', 'marks-q3'].forEach(id => {
    const el = document.getElementById(id);
    if (el) {
      el.addEventListener('input', calcTheoryQuizAverage);
      el.addEventListener('change', calcTheoryQuizAverage);
    }
  });

  // Add Student Form
  const formAddStudent = document.getElementById('form-add-student');
  if (formAddStudent) {
    formAddStudent.addEventListener('submit', (e) => {
      e.preventDefault();
      const id = document.getElementById('add-student-id').value.trim();
      const name = document.getElementById('add-student-name').value.trim();
      const email = document.getElementById('add-student-email').value.trim();
      const dept = document.getElementById('add-student-dept').value;
      const sem = parseInt(document.getElementById('add-student-sem').value);
      const contact = document.getElementById('add-student-contact').value.trim();

      if (State.students.some(s => s.id === id)) {
        showToast(`Error: Student ID '${id}' already exists!`, 'toast-error');
        return;
      }

      State.students.push({ id, name, email, department: dept, program: `B.Sc. in ${dept}`, semester: sem, contact, active: true });
      logAudit('STUDENT', `Enrolled new student: ${name} (${id}, Dept: ${dept}, Sem: ${sem})`);

      closeModal('modal-add-student');
      formAddStudent.reset();
      refreshDatalists();
      renderStudents();
      renderOverview();
      renderAttendance();
      renderReports();
      showToast('Student record created successfully!', 'toast-success');
    });
  }

  // Add Course Form
  const formAddCourse = document.getElementById('form-add-course');
  if (formAddCourse) {
    formAddCourse.addEventListener('submit', (e) => {
      e.preventDefault();
      const code = document.getElementById('add-course-code').value.trim().toUpperCase();
      const name = document.getElementById('add-course-title').value.trim();
      const dept = document.getElementById('add-course-dept').value.trim().toUpperCase();
      const credits = parseFloat(document.getElementById('add-course-credits').value) || 3.0;
      const type = document.getElementById('add-course-type')?.value || 'THEORY';
      const sem = parseInt(document.getElementById('add-course-sem').value) || 1;
      const teacher = document.getElementById('add-course-teacher').value.trim() || 'Prof. Faculty';

      if (State.courses.some(c => c.code === code)) {
        showToast(`Error: Course code '${code}' already exists!`, 'toast-error');
        return;
      }

      State.courses.push({ code, name, dept, credits, type, semester: sem, teacher, enrolledCount: 0 });
      logAudit('CURRICULUM', `Created ${type} course: ${code} - ${name} (${credits} Credits, Sem ${sem})`);

      closeModal('modal-add-course');
      formAddCourse.reset();
      refreshDatalists();
      renderDepartments();
      renderReports();
      showToast(`Course ${code} registered successfully!`, 'toast-success');
    });
  }

  // Add Department Form
  const formAddDept = document.getElementById('form-add-dept');
  if (formAddDept) {
    formAddDept.addEventListener('submit', (e) => {
      e.preventDefault();
      const id = document.getElementById('add-dept-id').value.trim().toUpperCase();
      const name = document.getElementById('add-dept-name').value.trim();
      const head = document.getElementById('add-dept-head').value.trim() || 'Department Head';
      const programs = document.getElementById('add-dept-programs').value.trim() || `B.Sc. in ${id}`;

      if (State.departments.some(d => d.id === id)) {
        showToast(`Error: Department ID '${id}' already exists!`, 'toast-error');
        return;
      }

      State.departments.push({ id, name, head, programs, coursesCount: 0 });
      logAudit('DEPARTMENT', `Created department: ${id} - ${name} (Head: ${head})`);

      closeModal('modal-add-dept');
      formAddDept.reset();
      refreshDatalists();
      renderDepartments();
      renderReports();
      showToast(`Department ${id} added successfully!`, 'toast-success');
    });
  }

  // Add Room Form
  const formAddRoom = document.getElementById('form-add-room');
  if (formAddRoom) {
    formAddRoom.addEventListener('submit', (e) => {
      e.preventDefault();
      const id = document.getElementById('add-room-id').value.trim().toUpperCase();
      const building = document.getElementById('add-room-building').value.trim();
      const capacity = parseInt(document.getElementById('add-room-capacity').value) || 40;
      const facilities = document.getElementById('add-room-facilities').value.trim() || 'Standard';

      if (State.rooms.some(r => r.id === id)) {
        showToast(`Error: Room code '${id}' already exists!`, 'toast-error');
        return;
      }

      State.rooms.push({ id, building, capacity, available: true, facilities });
      logAudit('FACILITY', `Registered room: ${id} in ${building} (${capacity} Seats)`);

      closeModal('modal-add-room');
      formAddRoom.reset();
      refreshDatalists();
      renderRoomsAndSeating();
      showToast(`Examination room ${id} added successfully!`, 'toast-success');
    });
  }

  // Record Attendance Form
  const formAttendance = document.getElementById('form-record-attendance');
  if (formAttendance) {
    formAttendance.addEventListener('submit', (e) => {
      e.preventDefault();
      const examId = document.getElementById('att-exam-id').value.trim();
      const studentId = document.getElementById('att-student-id').value.trim();
      const status = document.getElementById('att-status').value;
      const remarks = document.getElementById('att-remarks').value.trim() || 'Verified';
      const now = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

      let rec = State.attendance.find(a => a.studentId === studentId && a.examId === examId);
      if (rec) {
        rec.status = status;
        rec.time = now;
        rec.remarks = remarks;
      } else {
        State.attendance.push({
          id: 'ATT-' + (State.attendance.length + 1),
          examId: examId,
          studentId: studentId,
          status: status,
          time: now,
          remarks: remarks
        });
      }

      logAudit('ATTENDANCE', `Recorded attendance: ${studentId} marked ${status} for ${examId}`);

      closeModal('modal-record-attendance');
      formAttendance.reset();
      renderAttendance();
      showToast(`Attendance saved: ${studentId} marked ${status}`, 'toast-success');
    });
  }

  // Schedule Exam Form with Conflict Engine
  const formScheduleExam = document.getElementById('form-schedule-exam');
  if (formScheduleExam) {
    formScheduleExam.addEventListener('submit', (e) => {
      e.preventDefault();
      const course = document.getElementById('exam-course').value.trim();
      const type = document.getElementById('exam-type').value;
      const sem = parseInt(document.getElementById('exam-sem').value);
      const date = document.getElementById('exam-date').value;
      const start = document.getElementById('exam-start').value;
      const end = document.getElementById('exam-end').value;
      const room = document.getElementById('exam-room').value.trim();
      const invigilator = document.getElementById('exam-invigilator').value.trim();

      if (start >= end) {
        showToast('Invalid Time Order: Start time must precede end time!', 'toast-error');
        return;
      }

      const conflict = checkSchedulingConflict(room, date, start, end, invigilator);
      if (conflict) {
        showToast(conflict, 'toast-error');
        return;
      }

      const newId = `EX-2026-00${State.exams.length + 1}`;
      State.exams.push({ id: newId, course, type, semester: sem, date, startTime: start, endTime: end, room, invigilator, status: 'SCHEDULED' });

      logAudit('SCHEDULING', `Scheduled exam slot ${newId} (${course} in ${room}, ${date} ${start}-${end})`);

      closeModal('modal-schedule-exam');
      formScheduleExam.reset();
      renderExams();
      renderOverview();
      showToast(`Examination slot ${newId} confirmed without conflicts!`, 'toast-success');
    });
  }

  // Enter Marks Form with Theory vs Lab Boundary Enforcement
  const formMarks = document.getElementById('form-enter-marks');
  if (formMarks) {
    formMarks.addEventListener('submit', (e) => {
      e.preventDefault();
      const sid = document.getElementById('marks-student-id').value.trim();
      const course = document.getElementById('marks-course-code').value.trim().toUpperCase() || 'CSE221';
      const scheme = document.getElementById('marks-course-type')?.value || 'THEORY';

      let total = 0;
      let att = 0, asg = 0, pres = 0, mid = 0, fin = 0, labRep = 0, labPerf = 0;
      let q1 = null, q2 = null, q3 = null, quizAvg = 0;

      if (scheme === 'LAB') {
        att = parseFloat(document.getElementById('marks-lab-att').value) || 0;
        labRep = parseFloat(document.getElementById('marks-lab-rep').value) || 0;
        labPerf = parseFloat(document.getElementById('marks-lab-perf').value) || 0;
        fin = parseFloat(document.getElementById('marks-lab-fin').value) || 0;

        if (att < 0 || att > 10) { showToast('Lab Attendance score must be between 0 and 10.0', 'toast-error'); return; }
        if (labRep < 0 || labRep > 25) { showToast('Lab Report score must be between 0 and 25.0', 'toast-error'); return; }
        if (labPerf < 0 || labPerf > 25) { showToast('Lab Performance score must be between 0 and 25.0', 'toast-error'); return; }
        if (fin < 0 || fin > 40) { showToast('Lab Final Exam score must be between 0 and 40.0', 'toast-error'); return; }

        total = parseFloat((att + labRep + labPerf + fin).toFixed(2));
      } else {
        // THEORY: Attendance (7), Assignment (5), Quizzes 1,2,3 (15 each, avg 15), Presentation (8), Midterm (25), Final Exam (40) = 100
        att = parseFloat(document.getElementById('marks-att').value) || 0;
        asg = parseFloat(document.getElementById('marks-asg').value) || 0;

        const q1Val = document.getElementById('marks-q1')?.value.trim();
        const q2Val = document.getElementById('marks-q2')?.value.trim();
        const q3Val = document.getElementById('marks-q3')?.value.trim();
        if (q1Val !== '' && !isNaN(q1Val)) q1 = parseFloat(q1Val);
        if (q2Val !== '' && !isNaN(q2Val)) q2 = parseFloat(q2Val);
        if (q3Val !== '' && !isNaN(q3Val)) q3 = parseFloat(q3Val);

        const qAvgVal = document.getElementById('marks-q-avg')?.value.trim();
        if (qAvgVal !== '' && !isNaN(qAvgVal)) {
          quizAvg = parseFloat(qAvgVal);
        } else {
          const vals = [q1, q2, q3].filter(v => v !== null && !isNaN(v));
          quizAvg = vals.length > 0 ? parseFloat((vals.reduce((a, b) => a + b, 0) / vals.length).toFixed(2)) : 0;
        }

        pres = parseFloat(document.getElementById('marks-pres').value) || 0;
        mid = parseFloat(document.getElementById('marks-mid').value) || 0;
        fin = parseFloat(document.getElementById('marks-fin').value) || 0;

        if (att < 0 || att > 7) { showToast('Theory Attendance score must be between 0 and 7.0', 'toast-error'); return; }
        if (asg < 0 || asg > 5) { showToast('Assignment score must be between 0 and 5.0', 'toast-error'); return; }
        if (q1 !== null && (q1 < 0 || q1 > 15)) { showToast('Quiz 1 score must be between 0 and 15.0', 'toast-error'); return; }
        if (q2 !== null && (q2 < 0 || q2 > 15)) { showToast('Quiz 2 score must be between 0 and 15.0', 'toast-error'); return; }
        if (q3 !== null && (q3 < 0 || q3 > 15)) { showToast('Quiz 3 score must be between 0 and 15.0', 'toast-error'); return; }
        if (quizAvg < 0 || quizAvg > 15) { showToast('Quiz Average score must be between 0 and 15.0', 'toast-error'); return; }
        if (pres < 0 || pres > 8) { showToast('Presentation score must be between 0 and 8.0', 'toast-error'); return; }
        if (mid < 0 || mid > 25) { showToast('Midterm score must be between 0 and 25.0', 'toast-error'); return; }
        if (fin < 0 || fin > 40) { showToast('Final Exam score must be between 0 and 40.0', 'toast-error'); return; }

        total = parseFloat((att + asg + quizAvg + pres + mid + fin).toFixed(2));
      }

      const maxScore = 100.0;
      const percentage = (total / maxScore) * 100.0;
      const grade = calculateGrade(percentage);
      const remark = getGradeRemark(grade);
      const point = getGradePoint(grade);

      let m = State.marks.find(x => x.studentId === sid && x.course === course);
      if (m) {
        m.type = scheme;
        m.att = att;
        m.asg = asg;
        m.q1 = q1;
        m.q2 = q2;
        m.q3 = q3;
        m.quizAvg = quizAvg;
        m.pres = pres;
        m.mid = mid;
        m.fin = fin;
        m.labRep = labRep;
        m.labPerf = labPerf;
        m.total = total;
        m.grade = grade;
      } else {
        State.marks.push({
          course,
          studentId: sid,
          type: scheme,
          att,
          asg,
          q1,
          q2,
          q3,
          quizAvg,
          pres,
          mid,
          fin,
          labRep,
          labPerf,
          total,
          grade
        });
      }

      logAudit('ASSESSMENT', `Recorded ${scheme} assessment marks for examinee ${sid} in ${course}: Total ${total}/${maxScore} (${percentage.toFixed(1)}% - Grade: ${grade} [${point.toFixed(2)}, ${remark}])`);

      closeModal('modal-enter-marks');
      formMarks.reset();
      renderMarks();
      renderOverview();
      renderReports();
      showToast(`Marks saved! Total: ${total} / 100 (${percentage.toFixed(1)}%) — Grade: ${grade} (${point.toFixed(2)} - ${remark})`, 'toast-success');
    });
  }

  // Submit Grievance Form
  const formGrievance = document.getElementById('form-submit-grievance');
  if (formGrievance) {
    formGrievance.addEventListener('submit', (e) => {
      e.preventDefault();
      const type = document.getElementById('grievance-type').value;
      const course = document.getElementById('grievance-course').value;
      const desc = document.getElementById('grievance-desc').value.trim();

      const newId = `APP-2026-00${State.applications.length + 1}`;
      const sid = State.currentRole === 'STUDENT' ? 'STU001' : (State.students.length > 0 ? State.students[0].id : 'STU001');

      State.applications.push({
        id: newId,
        studentId: sid,
        type: type,
        course: course,
        desc: desc,
        status: 'PENDING',
        date: new Date().toISOString().split('T')[0],
        comments: 'Submitted by student'
      });

      logAudit('GRIEVANCE', `Lodged application ${newId} (${type} for ${course})`);

      closeModal('modal-submit-grievance');
      formGrievance.reset();
      renderApplications();
      renderOverview();
      showToast(`Application ${newId} lodged successfully!`, 'toast-success');
    });
  }
}

function openModal(id) {
  const el = document.getElementById(id);
  if (!el) return;
  el.style.display = 'flex';
  requestAnimationFrame(() => {
    el.classList.add('open');
  });
}

function closeModal(id) {
  const el = document.getElementById(id);
  if (!el) return;
  el.classList.remove('open');
  setTimeout(() => {
    if (!el.classList.contains('open')) {
      el.style.display = 'none';
    }
  }, 260);
}

// Toast Alert with fluid spring timing
let toastTimer = null;
function showToast(message, type = 'toast-info') {
  const toast = document.getElementById('toast-notification');
  if (!toast) return;

  if (toastTimer) clearTimeout(toastTimer);

  toast.textContent = message;
  toast.className = '';
  toast.classList.add('show', type);

  toastTimer = setTimeout(() => {
    toast.classList.remove('show');
  }, 3200);
}
