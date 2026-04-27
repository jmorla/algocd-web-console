import './style.css'
import Alpine from 'alpinejs'
import 'htmx.org'

window.Alpine = Alpine
Alpine.start()

import { 
  createIcons, 
  LayoutDashboard, 
  Terminal, 
  GraduationCap, 
  Activity, 
  LineChart, 
  Settings, 
  Zap, 
  Cpu, 
  Database, 
  Globe, 
  HardDrive, 
  History, 
  Plus, 
  RefreshCw, 
  AlertTriangle, 
  ChevronRight, 
  TrendingUp, 
  Monitor, 
  User, 
  CreditCard, 
  LogOut, 
  ChevronUp, 
  Bot, 
  X, 
  Package, 
  Upload, 
  ArrowLeft, 
  ChevronDown, 
  UploadCloud, 
  FileCode, 
  Info,
  AlertCircle,
  CheckCircle,
  Rocket,
  Wallet,
  PieChart,
  RotateCcw,
  ChevronLeft,
  FileQuestion,
  MoreVertical,
  Settings2,
  Square,
  Trash2
  } from 'lucide'

  // Initialize Lucide icons
  function initIcons() {
  createIcons({
    icons: {
      LayoutDashboard, Terminal, GraduationCap, Activity, LineChart, Settings, 
      Zap, Cpu, Database, Globe, HardDrive, History, Plus, RefreshCw, 
      AlertTriangle, ChevronRight, TrendingUp, Monitor, User, CreditCard, 
      LogOut, ChevronUp, Bot, X, Package, Upload, ArrowLeft, ChevronDown, 
      UploadCloud,
      FileCode,
      Info,
      AlertCircle,
      CheckCircle,
      Rocket,
      Wallet,
      PieChart,
      RotateCcw,
      ChevronLeft,
      FileQuestion,
      MoreVertical,
      Settings2,
      Square,
      Trash2
      }  });

      }

// Profile Dropdown Logic
function initProfileDropdown() {
  const profileBtn = document.getElementById('profile-btn');
  const profileDropdown = document.getElementById('profile-dropdown');
  const profileArrow = document.getElementById('profile-arrow');

  if (profileBtn && profileDropdown && !profileBtn.dataset.listenerAdded) {
    profileBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      profileDropdown.classList.toggle('hidden');
      profileArrow.classList.toggle('rotate-180');
    });

    document.addEventListener('click', () => {
      if (!profileDropdown.classList.contains('hidden')) {
        profileDropdown.classList.add('hidden');
        profileArrow.classList.remove('rotate-180');
      }
    });
    
    profileBtn.dataset.listenerAdded = 'true';
  }
}

// Run initializers
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => {
    initIcons();
    initProfileDropdown();
  });
} else {
  initIcons();
  initProfileDropdown();
}

// HTMX: Re-initialize icons after content swap
document.addEventListener('htmx:afterSwap', () => {
  initIcons();
});
