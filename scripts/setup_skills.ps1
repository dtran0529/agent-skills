param(
  [ValidateSet("codex", "claude", "both")]
  [string]$Agent = "both",
  [string]$Target = "",
  [switch]$Force,
  [switch]$Prune,
  [switch]$Refresh
)

$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)

if ($Refresh) {
  $Force = $true
  $Prune = $true
}

function Resolve-Targets {
  if ($Target -ne "") {
    return @($Target)
  }
  $codexDir = if ($env:CODEX_HOME) { Join-Path $env:CODEX_HOME "skills" } else { Join-Path $HOME ".codex/skills" }
  $claudeDir = Join-Path $HOME ".claude/skills"
  switch ($Agent) {
    "codex"  { return @($codexDir) }
    "claude" { return @($claudeDir) }
    "both"   { return @($codexDir, $claudeDir) }
  }
}

function Write-LinkStatus {
  param([string]$Action, [string]$SkillName, [string]$Detail)
  Write-Host ("{0,-5} {1} {2}" -f $Action, $SkillName, $Detail)
}

function Link-Into {
  param([string]$TargetDir)

  New-Item -ItemType Directory -Force -Path $TargetDir | Out-Null
  $RepoSkills = @{}

  Get-ChildItem -Path $RootDir -Directory | ForEach-Object {
    $skillDir = $_.FullName
    $skillName = $_.Name
    $skillFile = Join-Path $skillDir "SKILL.md"
    if (-not (Test-Path $skillFile)) { return }

    $RepoSkills[$skillName] = $true
    $linkPath = Join-Path $TargetDir $skillName

    if (Test-Path $linkPath) {
      $item = Get-Item $linkPath -Force
      if ($item.LinkType -eq "SymbolicLink" -or $item.LinkType -eq "Junction") {
        $currentTarget = $item.Target
        if ($currentTarget -eq $skillDir) {
          Write-LinkStatus "OK" $skillName "-> $currentTarget"
          return
        }
        if ($Force) {
          Remove-Item $linkPath -Force
        } else {
          Write-LinkStatus "SKIP" $skillName "(already linked to $currentTarget)"
          return
        }
      } else {
        Write-LinkStatus "SKIP" $skillName "(path exists and is not a link): $linkPath"
        return
      }
    }

    try {
      New-Item -ItemType SymbolicLink -Path $linkPath -Target $skillDir | Out-Null
    } catch {
      New-Item -ItemType Junction -Path $linkPath -Target $skillDir | Out-Null
    }
    Write-LinkStatus "LINK" $skillName "-> $skillDir"
  }

  if ($Prune) {
    Get-ChildItem -Path $TargetDir -Force | ForEach-Object {
      if ($_.LinkType -eq "SymbolicLink" -or $_.LinkType -eq "Junction") {
        $name = $_.Name
        if (-not $RepoSkills.ContainsKey($name)) {
          Remove-Item $_.FullName -Force
          Write-LinkStatus "PRUNE" $name ""
        }
      }
    }
  }

  Write-Host "Done. Skills linked into: $TargetDir"
}

foreach ($dir in Resolve-Targets) {
  Write-Host "==> $dir"
  Link-Into -TargetDir $dir
}
