%define name sqladmin
%define version 0.2.2
%define release 1
%define prefix /usr

Summary: A small JDBC client tool
Name: %{name}
Version: %{version}
Release: %{release}
License: LGPL
Group: Applications/Database
URL: http://sqladmin.sf.net/
Source: %{name}-%{version}.tar.gz
BuildRoot: %{_tmppath}/%{name}-root
Vendor: TUXPAN Software
AutoReqProv: no
Requires: swt >= 3.0
BuildRequires: swt >= 3.0

%define sourcedir $RPM_SOURCE_DIR/%{name}-%{version}
%define source %{name}-%{version}.tar.gz
%define jardir %{prefix}/lib/java
%define bindir %{prefix}/bin
%define docdir %{prefix}/share/doc/%{name}-%{version}
%define pixdir %{prefix}/share/pixmaps
%define appdir %{prefix}/share/applications


%description
SQLAdmin is Java Database Conectivity client tool.
 

%prep
cd $RPM_SOURCE_DIR
tar xzf %{source}
cd %{sourcedir}
echo "#!/bin/sh" > run.sh
echo "java -Djava.library.path=/usr/lib -jar /usr/lib/java/%{name}-%{version}.jar" >> run.sh

%build
cd %{sourcedir}
ant dist-bin -Dversion=%{version}

%install
mkdir -p %{buildroot}/%{jardir}
mkdir -p %{buildroot}/%{docdir}
mkdir -p %{buildroot}/%{bindir}
mkdir -p %{buildroot}/%{pixdir}
mkdir -p %{buildroot}/%{appdir}
install -m 755 %{sourcedir}/run.sh %{buildroot}/%{bindir}/sqladmin
cp %{sourcedir}/%{name}-%{version}.jar  %{buildroot}/%{jardir}
cp %{sourcedir}/README %{buildroot}/%{docdir}
cp %{sourcedir}/ChangeLog %{buildroot}/%{docdir}
cp %{sourcedir}/sqladmin.png %{buildroot}/%{pixdir}
cp %{sourcedir}/sqladmin.desktop %{buildroot}/%{appdir}

%clean
rm -rf %{buildroot}
rm -rf %{sourcedir}

%files
%defattr(-, root, root)
%doc %{docdir}/* 
%{jardir}/*.jar
%{bindir}/*
%{pixdir}/*
%{appdir}/*


%changelog
* Wed Dec 31 2003 Franco Catrin <fcatrin@tuxpan.com>
- Initial RPM release.

